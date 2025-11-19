package py.edu.uc.lp32025.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper base genérico con conversiones comunes entidad ↔ DTO.
 */
public abstract class BaseMapper<E, D> {

    private final Class<E> entityClass;
    private final Class<D> dtoClass;

    protected BaseMapper(Class<E> entityClass, Class<D> dtoClass) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    public D toDto(E entity) {
        if (entity == null) return null;
        try {
            D dto = dtoClass.getDeclaredConstructor().newInstance();
            copyEntityToDto(entity, dto);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error convirtiendo entidad a DTO", e);
        }
    }

    public E toEntity(D dto) {
        if (dto == null) return null;
        try {
            E entity = entityClass.getDeclaredConstructor().newInstance();
            copyDtoToEntity(dto, entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error convirtiendo DTO a entidad", e);
        }
    }

    public List<D> toDtoList(List<E> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<E> toEntityList(List<D> dtos) {
        if (dtos == null) return null;
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public Page<D> toDtoPage(Page<E> entityPage) {
        if (entityPage == null) return Page.empty();
        List<D> dtoList = toDtoList(entityPage.getContent());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    protected abstract void copyEntityToDto(E entity, D dto);

    protected abstract void copyDtoToEntity(D dto, E entity);
}