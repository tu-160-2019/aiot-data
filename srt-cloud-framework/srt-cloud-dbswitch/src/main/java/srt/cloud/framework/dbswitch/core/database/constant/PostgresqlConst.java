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

public final class PostgresqlConst {

  public static final String TPL_KEY_SCHEMA = "<SCHEMA>";
  public static final String TPL_KEY_TABLE = "<TABLE>";

  public static final String CREATE_TABLE_SQL_TPL =
      "WITH tabobj as (\n"
          + " select pg_class.relfilenode as oid,pg_namespace.nspname as nspname,pg_class.relname as relname\n"
          + " from pg_catalog.pg_class \n"
          + " join pg_catalog.pg_namespace on pg_class.relnamespace = pg_namespace.oid \n"
          + " where pg_namespace.nspname='<SCHEMA>' and pg_class.relname ='<TABLE>'\n"
          + "),\n"
          + "attrdef AS (\n"
          + "    SELECT \n"
          + "        n.nspname,\n"
          + "        c.relname,\n"
          + "        pg_catalog.array_to_string(c.reloptions || array(select 'toast.' || x from pg_catalog.unnest(tc.reloptions) x), ', ') as relopts,\n"
          + "        c.relpersistence,\n"
          + "        a.attnum,\n"
          + "        a.attname,\n"
          + "        pg_catalog.format_type(a.atttypid, a.atttypmod) as atttype,\n"
          + "        (SELECT substring(pg_catalog.pg_get_expr(d.adbin, d.adrelid, true) for 128) \n"
          + "            FROM pg_catalog.pg_attrdef d\n"
          + "            WHERE d.adrelid = a.attrelid AND d.adnum = a.attnum AND a.atthasdef\n"
          + "         )as attdefault,\n"
          + "        a.attnotnull,\n"
          + "        (SELECT c.collname FROM pg_catalog.pg_collation c, pg_catalog.pg_type t\n"
          + "            WHERE c.oid = a.attcollation AND t.oid = a.atttypid AND a.attcollation <> t.typcollation\n"
          + "        ) as attcollation,\n"
          + "        a.attidentity,\n"
          + "        '' as attgenerated\n"
          + "    FROM pg_catalog.pg_attribute a\n"
          + "    JOIN pg_catalog.pg_class c ON a.attrelid = c.oid\n"
          + "    JOIN pg_catalog.pg_namespace n ON c.relnamespace = n.oid\n"
          + "    LEFT JOIN pg_catalog.pg_class tc ON (c.reltoastrelid = tc.oid), tabobj\n"
          + "    WHERE n.nspname =tabobj.nspname \n"
          + "        AND c.relname =tabobj.relname\n"
          + "        AND a.attnum > 0\n"
          + "        AND NOT a.attisdropped\n"
          + "    ORDER BY a.attnum\n"
          + "),\n"
          + "coldef AS (\n"
          + "    SELECT\n"
          + "        attrdef.nspname,\n"
          + "        attrdef.relname,\n"
          + "        attrdef.relopts,\n"
          + "        attrdef.relpersistence,\n"
          + "        pg_catalog.format(\n"
          + "            '%I %s%s%s%s%s',\n"
          + "            attrdef.attname,\n"
          + "            attrdef.atttype,\n"
          + "            case when attrdef.attcollation is null then '' else pg_catalog.format(' COLLATE %I', attrdef.attcollation) end,\n"
          + "            case when attrdef.attnotnull then ' NOT NULL' else '' end,\n"
          + "            case when attrdef.attdefault is null then ''\n"
          + "                else case when attrdef.attgenerated = 's' then pg_catalog.format(' GENERATED ALWAYS AS (%s) STORED', attrdef.attdefault)\n"
          + "                    when attrdef.attgenerated <> '' then ' GENERATED AS NOT_IMPLEMENTED'\n"
          + "                    else pg_catalog.format(' DEFAULT %s', attrdef.attdefault)\n"
          + "                end\n"
          + "            end,\n"
          + "            case when attrdef.attidentity<>'' then pg_catalog.format(' GENERATED %s AS IDENTITY',\n"
          + "                    case attrdef.attidentity when 'd' then 'BY DEFAULT' when 'a' then 'ALWAYS' else 'NOT_IMPLEMENTED' end)\n"
          + "                else '' end\n"
          + "        ) as col_create_sql\n"
          + "    FROM attrdef\n"
          + "    ORDER BY attrdef.attnum\n"
          + "),\n"
          + "tabdef AS (\n"
          + "    SELECT\n"
          + "        coldef.nspname,\n"
          + "        coldef.relname,\n"
          + "        coldef.relopts,\n"
          + "        coldef.relpersistence,\n"
          + "        string_agg(coldef.col_create_sql, E',\\n    ') as cols_create_sql\n"
          + "    FROM coldef\n"
          + "    GROUP BY\n"
          + "        coldef.nspname, coldef.relname, coldef.relopts, coldef.relpersistence\n"
          + ")\n"
          + "SELECT\n"
          + "    format(\n"
          + "        'CREATE%s TABLE %I.%I%s%s%s;',\n"
          + "        case tabdef.relpersistence when 't' then ' TEMP' when 'u' then ' UNLOGGED' else '' end,\n"
          + "        tabdef.nspname,\n"
          + "        tabdef.relname,\n"
          + "        coalesce(\n"
          + "            (SELECT format(E'\\n    PARTITION OF %I.%I %s\\n', pn.nspname, pc.relname, pg_get_expr(c.relpartbound, c.oid))\n"
          + "                FROM pg_class c JOIN pg_inherits i ON c.oid = i.inhrelid\n"
          + "                JOIN pg_class pc ON pc.oid = i.inhparent\n"
          + "                JOIN pg_namespace pn ON pn.oid = pc.relnamespace\n"
          + "                join tabobj on c.oid=tabobj.oid\n"
          + "             ),\n"
          + "            format(E' (\\n    %s\\n)', tabdef.cols_create_sql)\n"
          + "        ),\n"
          + "        case when tabdef.relopts <> '' then format(' WITH (%s)', tabdef.relopts) else '' end,\n"
          + "        coalesce(E'\\nPARTITION BY '||pg_get_partkeydef(tabobj.oid), '')\n"
          + "    ) as table_create_sql\n"
          + "FROM tabdef,tabobj";

  private PostgresqlConst() {

  }
}
