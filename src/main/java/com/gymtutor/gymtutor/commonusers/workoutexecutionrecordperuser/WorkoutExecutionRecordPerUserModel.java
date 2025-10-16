package com.gymtutor.gymtutor.commonusers.workoutexecutionrecordperuser;

import com.gymtutor.gymtutor.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "tb_workout_execution_record_per_user")
public class WorkoutExecutionRecordPerUserModel {

    @EmbeddedId
    private WorkoutExecutionRecordPerUserId workoutExecutionRecordPerUserId;

    // usa a PK do User (referencedColumnName removido)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "execution_count")
    private Short executionCount;

    @Column(name = "last_execution_time")
    private LocalDateTime lastExecutionTime;

    public WorkoutExecutionRecordPerUserId getWorkoutExecutionRecordPerUserId() {
        return workoutExecutionRecordPerUserId;
    }

    public void setWorkoutExecutionRecordPerUserId(WorkoutExecutionRecordPerUserId workoutExecutionRecordPerUserId) {
        this.workoutExecutionRecordPerUserId = workoutExecutionRecordPerUserId;
    }

    public User getUser() {
        return user;
    }

    // Setter necessário para associar o User gerenciado antes do save
    public void setUser(User user) {
        this.user = user;
    }

    public Short getExecutionCount() {
        return executionCount;
    }

    public void setExecutionCount(Short executionCount) {
        this.executionCount = executionCount;
    }

    public LocalDateTime getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(LocalDateTime lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }
}
