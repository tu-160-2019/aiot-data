package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbScreenApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:23
 */
public interface ScreenApiRepository extends JpaRepository<TbScreenApi, Long>, QuerydslPredicateExecutor<TbScreenApi> {

    List<TbScreenApi> findByScreenId(Long screenId);

    void deleteByScreenId(Long screenId);
}
