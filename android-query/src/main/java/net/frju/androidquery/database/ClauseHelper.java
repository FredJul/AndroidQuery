/**
 * Copyright 2013-present memtrip LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.frju.androidquery.database;

import net.frju.androidquery.operation.condition.And;
import net.frju.androidquery.operation.condition.Condition;
import net.frju.androidquery.operation.condition.In;
import net.frju.androidquery.operation.condition.On;
import net.frju.androidquery.operation.condition.Or;
import net.frju.androidquery.operation.condition.Where;
import net.frju.androidquery.operation.join.CrossInnerJoin;
import net.frju.androidquery.operation.join.InnerJoin;
import net.frju.androidquery.operation.join.Join;
import net.frju.androidquery.operation.join.LeftOuterJoin;
import net.frju.androidquery.operation.join.NaturalInnerJoin;
import net.frju.androidquery.operation.join.NaturalLeftOuterJoin;
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
    private static final String IN = "IN";
    private static final String AND = "AND";
    private static final String OR = "OR";

    protected ClauseHelper() { }

    public String getCondition(Condition[] condition) {
        StringBuilder clauseBuilder = new StringBuilder();

        if (condition != null && condition.length > 0) {
            if (condition.length == 1) {
                clauseBuilder.append(getCondition(condition[0]));
            } else {
                clauseBuilder.append(getCondition(Condition.and(condition)));
            }
        }

        return clauseBuilder.toString();
    }

    private String getCondition(Condition condition) {
        StringBuilder clauseBuilder = new StringBuilder();

        if (condition instanceof In) {
            clauseBuilder.append(buildInCondition((In) condition));
        } else if (condition instanceof Where) {
            clauseBuilder.append(buildWhereCondition((Where) condition));
        } else if (condition instanceof On) {
            clauseBuilder.append(buildOnCondition((On) condition));
        } else if (condition instanceof And) {
            clauseBuilder.append(BRACKET_START);
            And and = (And) condition;
            for (Condition item : and.getCondition()) {
                clauseBuilder.append(getCondition(item));
                clauseBuilder.append(SPACE);
                clauseBuilder.append(AND);
                clauseBuilder.append(SPACE);
            }

            // remove the excess AND with its 2 spaces
            clauseBuilder.delete(clauseBuilder.length() - 5, clauseBuilder.length());
            clauseBuilder.append(BRACKET_END);
        } else if (condition instanceof Or) {
            clauseBuilder.append(BRACKET_START);
            Or or = (Or) condition;
            for (Condition item : or.getCondition()) {
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

    private String buildWhereCondition(Where where) {
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

    private String buildOnCondition(On on) {
        String stringBuilder = "ON" +
                SPACE +
                on.getColumn1() +
                SPACE +
                "=" +
                SPACE +
                on.getColumn2();

        return stringBuilder;
    }

    public String[] getConditionArgs(Condition[] condition) {
        List<String> args = new ArrayList<>();

        if (condition != null) {
            for (Condition item : condition) {
                args.addAll(getConditionArgs(item));
            }
        }

        return args.toArray(new String[args.size()]);
    }

    private List<String> getConditionArgs(Condition condition) {
        List<String> args = new ArrayList<>();

        if (condition instanceof In) {
            args.addAll(buildInArgs((In) condition));
        } else if (condition instanceof Where) {
            String arg = buildWhereArgs((Where) condition);
            if (arg != null) {
                args.add(arg);
            }
        } else if (condition instanceof And) {
            And and = (And) condition;
            for (Condition item : and.getCondition()) {
                args.addAll(getConditionArgs(item));
            }
        } else if (condition instanceof Or) {
            Or or = (Or) condition;
            for (Condition item : or.getCondition()) {
                args.addAll(getConditionArgs(item));
            }
        }

        return args;
    }

    private String buildWhereArgs(Where where) {
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

    public String getJoinStatement(Join[] joins, Resolver resolver) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Join join : joins) {
            TableDescription tableDescription = resolver.getTableDescription(join.getTable());
            String tableRealName = tableDescription.getTableRealName();

            stringBuilder
            		.append(" ")
            		.append(getJoinType(join))
                    .append(" ")
                    .append(tableRealName)
                    .append(" ")
                    .append(getCondition(join.getClauses()));

            if (join.getJoin() != null) {
                stringBuilder.append(" ")
                        .append(getJoinStatement(new Join[] { join.getJoin() }, resolver));
            }
        }

        return stringBuilder.toString();
    }

    public String buildJoinQuery(String[] tableColumns, Join[] joins, String tableName, Condition[] condition,
                                 OrderBy[] orderBy, Limit limit, Resolver resolver) {

        String[] joinColumns = getJoinColumns(joins, resolver);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("SELECT ");

        for (String column : tableColumns) {
            stringBuilder.append(column).append(", ");
        }

        for (String column : joinColumns) {
            String columnAlias = column.replace(".","_");
            stringBuilder.append(column)
                    .append(" as ")
                    .append(columnAlias)
                    .append(", ");
        }

        // remove the trailing comma
        stringBuilder.delete(stringBuilder.length()-2, stringBuilder.length());

        String clauseString = getCondition(condition);
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
            TableDescription tableDescription = resolver.getTableDescription(join.getTable());
            String[] columnNames = tableDescription.getColumnNamesWithTablePrefix();

            Collections.addAll(joinColumns, columnNames);

            if (join.getJoin() != null) {
                String[] innerJoinColumns = getJoinColumns(new Join[] { join.getJoin() }, resolver);
                Collections.addAll(joinColumns, innerJoinColumns);
            }
        }

        String[] columns = new String[joinColumns.size()];
        return joinColumns.toArray(columns);
    }

    private String getJoinType(Join join) {
        if (join instanceof InnerJoin) {
            return "INNER JOIN";
        } else if (join instanceof CrossInnerJoin) {
            return "CROSS INNER JOIN";
        } else if (join instanceof LeftOuterJoin) {
            return "LEFT OUTER JOIN";
        } else if (join instanceof NaturalInnerJoin) {
            return "NATURAL INNER JOIN";
        } else if (join instanceof NaturalLeftOuterJoin) {
            return "NATURAL LEFT OUTER JOIN";
        }

        return "INNER JOIN";
    }
}
