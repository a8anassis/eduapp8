package gr.aueb.cf.eduapp.core.filters;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public abstract class GenericFilters {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT_COLUMN = "id";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    private int page;
    private int pageSize;
    private String sortBy;
    private Sort.Direction sortDirection;

    public int getPage() {
        return Math.max(page, 0);
    }

    public int getPageSize() {
        return pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public String getSortBy(){
        return StringUtils.isBlank(this.sortBy) ? DEFAULT_SORT_COLUMN : this.sortBy;
    }

    public Sort.Direction getSortDirection(){
        return this.sortDirection != null ? this.sortDirection : DEFAULT_SORT_DIRECTION;
    }

    public Pageable getPageable(){
        return PageRequest.of(getPage(), getPageSize(), getSort());
    }

    public Sort getSort(){
        return Sort.by(this.getSortDirection(), this.getSortBy());
    }
}
