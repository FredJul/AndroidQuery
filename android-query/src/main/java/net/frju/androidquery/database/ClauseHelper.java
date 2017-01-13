/**
 * Copyright 2013-present memtrip LTD.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License isEqualTo distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.frju.androidquery.database;

import net.frju.androidquery.operation.condition.And;
import net.frju.androidquery.operation.condition.Between;
import net.frju.androidquery.operation.condition.Compare;
import net.frju.androidquery.operation.condition.In;
import net.frju.androidquery.operation.condition.Or;
import net.frju.androidquery.operation.condition.Where;
import net.frju.androidquery.operation.join.Join;
import net.frju.androidquery.operation.keyword.Limit;
import net.frju.androidquery.operation.keyword.OrderBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class ClauseHelper {
    private static final String SPACE = " ";
    private static final String VALUE = "?";
    private static final String NULL = "NULL";
    private static final String BRACKET_START = "(";
    private static final String BRACKET_END = ")";
    private static final String COMMA = ",";
    private static final String NOT = "NOT";
    private static final String IN = "IN";
    private static final String BETWEEN = "BETWEEN";
    private static final String AND = "AND";
    private static final String OR = "OR";
    private static final String ON = "ON";
    private static final String COLLATE = "COLLATE";

    protected ClauseHelper() {
    }

    public String getCondition(Where[] where) {
        StringBuilder clauseBuilder = new StringBuilder();

        if (where != null && where.length > 0) {
            if (where.length == 1) {
                clauseBuilder.append(getCondition(where[0]));
            } else {
                clauseBuilder.append(getCondition(Where.combinesWithAnd(where)));
            }
        }

        return clauseBuilder.toString();
    }

    private String getCondition(Where where) {
        StringBuilder clauseBuilder = new StringBuilder();

        if (where instanceof In) {
            clauseBuilder.append(buildInCondition((In) where));
        } else if (where instanceof Between) {
            clauseBuilder.append(buildBetweenCondition((Between) where));
        } else if (where instanceof Compare) {
            clauseBuilder.append(buildCompareCondition((Compare) where));
        } else if (where instanceof And) {
            clauseBuilder.append(BRACKET_START);
            And and = (And) where;
            for (Where item : and.getCondition()) {
                clauseBuilder.append(getCondition(item));
                clauseBuilder.append(SPACE);
                clauseBuilder.append(AND);
                clauseBuilder.append(SPACE);
            }

            // remove the excess AND with its 2 spaces
            clauseBuilder.delete(clauseBuilder.length() - 5, clauseBuilder.length());
            clauseBuilder.append(BRACKET_END);
        } else if (where instanceof Or) {
            clauseBuilder.append(BRACKET_START);
            Or or = (Or) where;
            for (Where item : or.getCondition()) {
                clauseBuilder.append(getCondition(item));
                clauseBuilder.append(SPACE);
                clauseBuilder.append(OR);
                clauseBuilder.append(SPACE);
            }

            // remove the excess OR with its 2 spaces
            clauseBuilder.delete(clauseBuilder.length() - 4, clauseBuilder.length());
            clauseBuilder.append(BRACKET_END);
        }

        return clauseBuilder.toString();
    }

    private String buildCompareCondition(Compare where) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(where.getColumn());
        stringBuilder.append(SPACE);
        stringBuilder.append(where.getOperator().toString());
        stringBuilder.append(SPACE);
        if (where.getValue() == null) {
            stringBuilder.append(NULL);
        } else {
            stringBuilder.append(VALUE);
        }

        return stringBuilder.toString();
    }

    private String buildInCondition(In in) {
        String row = in.getColumn();
        int length = in.getValues().length;

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(row);
        if (in.hasNot()) {
            stringBuilder.append(SPACE);
            stringBuilder.append(NOT);
        }
        stringBuilder.append(SPACE);
        stringBuilder.append(IN);
        stringBuilder.append(SPACE);

        stringBuilder.append(BRACKET_START);

        if (length > 0) {
            for (int i = 0; i < length; i++) {
                stringBuilder.append(VALUE);
                stringBuilder.append(COMMA);
            }

            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        }

        stringBuilder.append(BRACKET_END);

        return stringBuilder.toString();
    }

    private String buildBetweenCondition(Between between) {
        String row = between.getColumn();

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(row);
        if (between.hasNot()) {
            stringBuilder.append(SPACE);
            stringBuilder.append(NOT);
        }
        stringBuilder.append(SPACE);
        stringBuilder.append(BETWEEN);
        stringBuilder.append(SPACE);
        stringBuilder.append(VALUE);
        stringBuilder.append(AND);
        stringBuilder.append(VALUE);

        return stringBuilder.toString();
    }

    public String[] getConditionArgs(Where[] where) {
        List<String> args = new ArrayList<>();

        if (where != null) {
            for (Where item : where) {
                args.addAll(getConditionArgs(item));
            }
        }

        return args.toArray(new String[args.size()]);
    }

    private List<String> getConditionArgs(Where where) {
        List<String> args = new ArrayList<>();

        if (where instanceof In) {
            args.addAll(buildInArgs((In) where));
        } else if (where instanceof Between) {
            args.addAll(buildBetweenArgs((Between) where));
        } else if (where instanceof Compare) {
            String arg = buildCompareArgs((Compare) where);
            if (arg != null) {
                args.add(arg);
            }
        } else if (where instanceof And) {
            And and = (And) where;
            for (Where item : and.getCondition()) {
                args.addAll(getConditionArgs(item));
            }
        } else if (where instanceof Or) {
            Or or = (Or) where;
            for (Where item : or.getCondition()) {
                args.addAll(getConditionArgs(item));
            }
        }

        return args;
    }

    private String buildCompareArgs(Compare where) {
        if (where.getValue() == null) {
            return null;
        } else {
            String value = String.valueOf(where.getValue());

            if (value.equals("true")) {
                return "1";
            } else if (value.equals("false")) {
                return "0";
            }

            return value;
        }
    }

    private List<String> buildBetweenArgs(Between between) {
        List<String> args = new ArrayList<>();

        args.add(String.valueOf(between.getValue1()));
        args.add(String.valueOf(between.getValue2()));

        return args;
    }

    private List<String> buildInArgs(In in) {
        List<String> args = new ArrayList<>();

        for (int i = 0; i < in.getValues().length; i++) {
            args.add(String.valueOf(in.getValues()[i]));
        }

        return args;
    }

    public String getOrderBy(OrderBy[] orderByArray) {
        StringBuilder stringBuilder = new StringBuilder();

        if (orderByArray != null && orderByArray.length > 0) {
            for (OrderBy orderBy : orderByArray) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(COMMA);
                }

                if (orderBy.getOrder() == OrderBy.Order.RANDOM) {
                    stringBuilder.append(orderBy.getOrder().toString()).append("()");
                } else {
                    stringBuilder.append(orderBy.getField());
                    stringBuilder.append(SPACE);
                    if (orderBy.getCollate() != null) {
                        stringBuilder.append(COLLATE);
                        stringBuilder.append(SPACE);
                        stringBuilder.append(orderBy.getCollate().toString());
                        stringBuilder.append(SPACE);
                    }
                    stringBuilder.append(orderBy.getOrder().toString());
                }
            }
        }

        return stringBuilder.toString();
    }

    public String getLimit(Limit limit) {
        StringBuilder stringBuilder = new StringBuilder();

        if (limit != null) {
            stringBuilder.append(limit.getStart());
            stringBuilder.append(COMMA);
            stringBuilder.append(limit.getEnd());
        }

        return stringBuilder.toString();
    }

    private String buildOnCondition(Join join, Resolver resolver) {
        DbModelDescriptor initialTableDesc = resolver.getDbModelDescriptor(join.getInitialTable());
        DbModelDescriptor addedTableDesc = resolver.getDbModelDescriptor(join.getAddedTable());

        String stringBuilder = ON +
                SPACE +
                initialTableDesc.getTableDbName() +
                "." +
                join.getInitialTableColumn() +
                SPACE +
                "=" +
                SPACE +
                addedTableDesc.getTableDbName() +
                "." +
                join.getAddedTableColumn();

        return stringBuilder;
    }

    public String getJoinStatement(Join[] joins, Resolver resolver) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Join join : joins) {
            DbModelDescriptor dbModelDescriptor = resolver.getDbModelDescriptor(join.getAddedTable());
            String table2RealName = dbModelDescriptor.getTableDbName();

            stringBuilder
                    .append(" ")
                    .append(getJoinType(join))
                    .append(" ")
                    .append(table2RealName)
                    .append(" ")
                    .append(buildOnCondition(join, resolver));
        }

        return stringBuilder.toString();
    }

    public String buildJoinQuery(String[] tableColumns, Join[] joins, String tableName, Where[] where,
                                 OrderBy[] orderBy, Limit limit, Resolver resolver) {

        String[] joinColumns = getJoinColumns(joins, resolver);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("SELECT ");

        for (String column : tableColumns) {
            stringBuilder.append(column).append(", ");
        }

        for (String column : joinColumns) {
            String columnAlias = column.replace(".", "_");
            stringBuilder.append(column)
                    .append(" as ")
                    .append(columnAlias)
                    .append(", ");
        }

        // remove the trailing comma
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());

        String clauseString = getCondition(where);
        if (clauseString != null && clauseString.length() > 0) {
            clauseString = "WHERE " + clauseString;
        }

        String orderByString = getOrderBy(orderBy);
        if (orderByString != null && orderByString.length() > 0) {
            orderByString = "ORDER BY " + orderByString;
        }

        String limitString = getLimit(limit);
        if (limitString != null && limitString.length() > 0) {
            limitString = "LIMIT " + limitString;
        }

        stringBuilder.append(" FROM ")
                .append(tableName)
                .append(" ")
                .append(getJoinStatement(joins, resolver))
                .append(" ")
                .append(clauseString)
                .append(" ")
                .append(orderByString)
                .append(" ")
                .append(limitString);

        return stringBuilder.toString();
    }

    private String[] getJoinColumns(Join[] joins, Resolver resolver) {
        List<String> joinColumns = new ArrayList<>();

        for (Join join : joins) {
            DbModelDescriptor dbModelDescriptor = resolver.getDbModelDescriptor(join.getAddedTable());
            String[] columnNames = dbModelDescriptor.getColumnNamesWithTablePrefix();

            Collections.addAll(joinColumns, columnNames);
        }

        String[] columns = new String[joinColumns.size()];
        return joinColumns.toArray(columns);
    }

    private String getJoinType(Join join) {
        switch (join.getType()) {
            case CROSS_INNER:
                return "CROSS INNER JOIN";
            case LEFT_OUTER:
                return "LEFT OUTER JOIN";
            case NATURAL_INNER:
                return "NATURAL INNER JOIN";
            case NATURAL_LEFT_OUTER:
                return "NATURAL LEFT OUTER JOIN";
            default:
                return "INNER JOIN";
        }
    }
}
