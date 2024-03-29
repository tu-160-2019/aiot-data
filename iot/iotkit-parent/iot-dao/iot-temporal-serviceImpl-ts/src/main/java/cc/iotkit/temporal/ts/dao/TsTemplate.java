package cc.iotkit.temporal.ts.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class TsTemplate extends JdbcTemplate {

    public TsTemplate() {
    }

    public TsTemplate(DataSource dataSource) {
        super(dataSource);
    }

    public TsTemplate(DataSource dataSource, boolean lazyInit) {
        super(dataSource, lazyInit);
    }
}
