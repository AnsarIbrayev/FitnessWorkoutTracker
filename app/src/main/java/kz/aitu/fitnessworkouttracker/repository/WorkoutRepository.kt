package kz.aitu.fitnessworkouttracker.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

data class Workout(
    val id: String = "",
    val title: String = "",
    val notes: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val durationMin: Int = 0
)

class WorkoutRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    private fun uidOrThrow(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("User is not signed in")

    private fun workoutsRef(uid: String): DatabaseReference =
        db.child("users").child(uid).child("workouts")

    fun observeWorkouts(): Flow<List<Workout>> = callbackFlow {
        val uid = uidOrThrow()
        val ref = workoutsRef(uid)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { child ->
                    val w = child.getValue(Workout::class.java)
                    if (w != null) w.copy(id = child.key ?: w.id) else null
                }.sortedByDescending { it.dateMillis }

                trySend(list).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeWorkoutById(id: String): Flow<Workout?> = callbackFlow {
        val uid = uidOrThrow()
        val ref = workoutsRef(uid).child(id)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val w = snapshot.getValue(Workout::class.java)
                trySend(w?.copy(id = id)).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun addWorkout(title: String, notes: String, durationMin: Int) {
        val uid = uidOrThrow()
        val ref = workoutsRef(uid).push()
        val id = ref.key ?: throw IllegalStateException("Failed to generate key")

        val workout = Workout(
            id = id,
            title = title.trim(),
            notes = notes.trim(),
            dateMillis = System.currentTimeMillis(),
            durationMin = durationMin
        )

        ref.setValueSuspend(workout)
    }

    suspend fun updateWorkout(id: String, title: String, notes: String, durationMin: Int) {
        val uid = uidOrThrow()
        val ref = workoutsRef(uid).child(id)

        val updates = mapOf(
            "title" to title.trim(),
            "notes" to notes.trim(),
            "durationMin" to durationMin
        )
        ref.updateChildrenSuspend(updates)
    }

    suspend fun deleteWorkout(id: String) {
        val uid = uidOrThrow()
        val ref = workoutsRef(uid).child(id)
        ref.removeValueSuspend()
    }
}

/** ---- small suspend helpers (без доп зависимостей) ---- */

private suspend fun DatabaseReference.setValueSuspend(value: Any?) =
    suspendCancellableCoroutine<Unit> { cont ->
        setValue(value) { error, _ ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }

private suspend fun DatabaseReference.updateChildrenSuspend(values: Map<String, Any?>) =
    suspendCancellableCoroutine<Unit> { cont ->
        updateChildren(values) { error, _ ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }

private suspend fun DatabaseReference.removeValueSuspend() =
    suspendCancellableCoroutine<Unit> { cont ->
        removeValue { error, _ ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
