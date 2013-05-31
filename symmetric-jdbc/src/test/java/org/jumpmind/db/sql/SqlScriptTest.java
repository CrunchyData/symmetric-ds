package org.jumpmind.db.sql;

import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.Assert;

import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.platform.JdbcDatabasePlatformFactory;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class SqlScriptTest {

    @Test
    public void testSimpleSqlScript() throws Exception {
        SingleConnectionDataSource ds = getDataSource();
        IDatabasePlatform platform = JdbcDatabasePlatformFactory.createNewPlatformInstance(ds, new SqlTemplateSettings(), true);
        SqlScript script = new SqlScript(getClass().getResource("sqlscript-simple.sql"), platform.getSqlTemplate());
        script.execute();
        JdbcTemplate template = new JdbcTemplate(ds);
        Assert.assertEquals(2, template.queryForInt("select count(*) from test"));
        Assert.assertEquals(3, template.queryForObject("select test from test where test_id=2", String.class).split("\r\n|\r|\n").length);
        ds.destroy();
    }
    
    private SingleConnectionDataSource getDataSource() throws Exception {
        Class.forName("org.h2.Driver");
        Connection c = DriverManager.getConnection("jdbc:h2:mem:sqlscript");
        return new SingleConnectionDataSource(c, true);
    }
}