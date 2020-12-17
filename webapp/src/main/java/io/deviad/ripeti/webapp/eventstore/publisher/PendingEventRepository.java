package io.deviad.ripeti.webapp.eventstore.publisher;

import io.deviad.ripeti.webapp.eventstore.EventDescriptor;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

interface PendingEventRepository extends ElasticsearchRepository<EventDescriptor, Long> {

    List<EventDescriptor> findTop100ByStatusOrderByOccurredAtAsc(EventDescriptor.Status status);

    default List<EventDescriptor> listPending() {
        return findTop100ByStatusOrderByOccurredAtAsc(EventDescriptor.Status.PENDING);
    }

}
