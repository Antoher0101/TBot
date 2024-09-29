package com.mawus.core.domain.rasp.scheduleBetStation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mawus.core.domain.rasp.Pagination;

import java.util.List;

public class ScheduleBetStation {

    private List<IntervalSegments> intervalSegments; // interval_segments
    private List<Segment> segments;
    private Search search;
    private Pagination pagination;

    public List<IntervalSegments> getIntervalSegments() {
        return intervalSegments;
    }

    @JsonProperty("interval_segments")
    public void setIntervalSegments(
            List<IntervalSegments> intervalSegments) {
        this.intervalSegments = intervalSegments;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
