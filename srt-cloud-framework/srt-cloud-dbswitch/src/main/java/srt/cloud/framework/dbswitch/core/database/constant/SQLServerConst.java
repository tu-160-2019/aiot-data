// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.core.database.constant;

public final class SQLServerConst {

  public static final String GET_CURRENT_CATALOG_SQL =
      "Select Name From Master..SysDataBases Where DbId=(Select Dbid From Master..SysProcesses Where Spid = @@spid)";

  // https://blog.csdn.net/AskTommorow/article/details/52370072
  public static final String CREATE_TABLE_SQL_TPL =
      "declare @schemaname varchar(1024)\n"
          + "declare @tabname varchar(1024)\n"
          + "set @schemaname='%s' \n"
          + "set @tabname='%s' \n"
          + "\n"
          + "if ( object_id('tempdb.dbo.#t') is not null)\n"
          + "begin\n"
          + "DROP TABLE #t\n"
          + "end\n"
          + "\n"
          + "select  'create table ['+db_name()+'].['+@schemaname+'].[' + so.name + '] (' + o.list + ')' \n"
          + "    + CASE WHEN tc.Constraint_Name IS NULL THEN '' ELSE 'ALTER TABLE ['+db_name()+'].['+@schemaname+'].[' + so.Name + '] ADD CONSTRAINT ' + tc.Constraint_Name  + ' PRIMARY KEY ' + ' (' + LEFT(j.List, Len(j.List)-1) + ')' END \n"
          + "    TABLE_DDL\n"
          + "into #t from    sysobjects so\n"
          + "cross apply\n"
          + "    (SELECT \n"
          + "        '  ['+column_name+'] ' + \n"
          + "        data_type + case data_type\n"
          + "            when 'sql_variant' then ''\n"
          + "            when 'text' then ''\n"
          + "            when 'ntext' then ''\n"
          + "            when 'xml' then ''\n"
          + "            when 'decimal' then '(' + cast(numeric_precision as varchar) + ', ' + cast(numeric_scale as varchar) + ')'\n"
          + "            else coalesce('('+case when character_maximum_length = -1 then 'MAX' else cast(character_maximum_length as varchar) end +')','') end + ' ' +\n"
          + "        case when exists ( \n"
          + "        select id from syscolumns\n"
          + "        where object_name(id)=so.name\n"
          + "        and name=column_name\n"
          + "        and columnproperty(id,name,'IsIdentity') = 1 \n"
          + "        ) then\n"
          + "        'IDENTITY(' + \n"
          + "        cast(ident_seed(so.name) as varchar) + ',' + \n"
          + "        cast(ident_incr(so.name) as varchar) + ')'\n"
          + "        else ''\n"
          + "        end + ' ' +\n"
          + "         (case when IS_NULLABLE = 'No' then 'NOT ' else '' end ) + 'NULL ' + \n"
          + "          case when information_schema.columns.COLUMN_DEFAULT IS NOT NULL THEN 'DEFAULT '+ information_schema.columns.COLUMN_DEFAULT ELSE '' END + ', ' \n"
          + "\n"
          + "     from information_schema.columns where table_schema=@schemaname and table_name = so.name\n"
          + "     order by ordinal_position\n"
          + "    FOR XML PATH('')) o (list)\n"
          + "left join\n"
          + "    information_schema.table_constraints tc\n"
          + "on  tc.Table_name       = so.Name\n"
          + "AND tc.Constraint_Type  = 'PRIMARY KEY'\n"
          + "cross apply\n"
          + "    (select '[' + Column_Name + '], '\n"
          + "     FROM   information_schema.key_column_usage kcu\n"
          + "     WHERE  kcu.Constraint_Name = tc.Constraint_Name\n"
          + "     ORDER BY\n"
          + "        ORDINAL_POSITION\n"
          + "     FOR XML PATH('')) j (list)\n"
          + "where   xtype = 'U'\n"
          + "AND name=@tabname\n"
          + "\n"
          + "select (\n"
          + " case when (\n"
          + "  select count(a.constraint_type)\n"
          + "  from information_schema.table_constraints a \n"
          + "  inner join information_schema.constraint_column_usage b\n"
          + "  on a.constraint_name = b.constraint_name\n"
          + "  where a.constraint_type = 'PRIMARY KEY' \n"
          + "  AND a.CONSTRAINT_SCHEMA = @schemaname\n"
          + "  and a.table_name = @tabname\n"
          + " )=1 then\n"
          + "  replace(table_ddl,', )ALTER TABLE',') ALTER TABLE')\n"
          + "  else \n"
          + "   SUBSTRING(table_ddl,1,len(table_ddl)-3)+')' \n"
          + " end\n"
          + ") from #t";

  private SQLServerConst() {

  }
}
