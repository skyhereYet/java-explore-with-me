package ru.practicum.explore.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.requests.model.ViewRequest;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.requests.model.Request;
import ru.practicum.explore.requests.model.StateRequest;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer>, QuerydslPredicateExecutor<Request> {

    @Query("select req.event.id As event, count(req.id) As hit " +
        "from Request as req " +
        "where (req.event IN (:events) AND (req.status = :stateRequest)) " +
        "group by req.event ")
    List<ViewRequest> findViewRequest(List<Event> events, StateRequest stateRequest);

    List<Request> findAllByStatusAndEvent(StateRequest statusRequest, Event event);

    List<Request> findAllByRequesterId(Integer requesterid);

    List<Request> findAllByRequesterIdAndEventId(Integer requesterid, Integer eventId);
}
