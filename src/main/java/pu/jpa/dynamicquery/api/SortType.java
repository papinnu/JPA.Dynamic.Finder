package pu.jpa.dynamicquery.api;

import org.springframework.data.domain.Sort;

/**
 * @author Plamen Uzunov
 */
public enum SortType {
    ASC,
    DESC;

    public Sort.Direction toDomainSort() {
        return name().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    public static SortType lookup(String name) {
        for (SortType b : SortType.values()) {
            if (b.name().equalsIgnoreCase(name)) {
                return b;
            }
        }
        return ASC; // default sorting method
    }

}
