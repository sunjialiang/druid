package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleBlockTest5 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "declare   l_cnt number; "
                     + //
                     "begin   l_cnt := 0;"
                     + //
                     "   for c1 in (select id || '' id" +//
                     "                from escrow_trade" + //
                     "               where out_order_id in" + //
                     "                  (select out_order_id from tab_ipay_out_order_ids)" + //
                     "          ) " + //
                     "  loop" + //
                     "      update ipay_contract" + //
                     "          set is_chargeback = 'N'" + //
                     "          where out_ref = c1.id        and is_chargeback <> 'N';      l_cnt := l_cnt + 1;     if (mod(l_cnt, 200) = 0) then       commit;     end if;     dbms_application_info.set_client_info(l_cnt || ' rows updated!');   end loop;    commit; exception   when others then     raise;"
                     + "     rollback; " + //
                     "end;;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(3, visitor.getTables().size());

         Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("escrow_trade")));
         Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("tab_ipay_out_order_ids")));
         Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ipay_contract")));

        Assert.assertEquals(7, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());

//         Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("departments", "department_id")));
    }
}
