package ru.practicum.explore.likes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.likes.model.Like;

@Repository
public interface LikesRepository extends JpaRepository<Like, Integer> {
    Like findByLikeOwnerIdAndEventId(int likeOwnerId, int eventId);
}
