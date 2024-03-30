package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:23
 */
public interface ScreenRepository extends JpaRepository<TbScreen, Long>, QuerydslPredicateExecutor<TbScreen> {

    TbScreen findByIsDefault(boolean isDefault);

    List<TbScreen> findByState(String state);
}
