package com.gymtutor.gymtutor.activities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MuscularGroupRepository extends JpaRepository<MuscularGroupModel, Byte> {
    MuscularGroupModel findByMuscularGroup(MuscularGroup muscularGroup);
}
