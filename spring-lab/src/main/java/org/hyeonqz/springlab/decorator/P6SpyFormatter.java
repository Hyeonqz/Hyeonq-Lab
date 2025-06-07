package org.hyeonqz.springlab.decorator;

import java.util.Locale;

import org.hibernate.engine.jdbc.internal.FormatStyle;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class P6SpyFormatter implements MessageFormattingStrategy {

    private static final String SPACE = " ";
    private static final String NEW_LINE = "\n";
    private static final String TAP = "\t";
    private static final String CREATE = "create";
    private static final String ALTER = "alter";
    private static final String DROP = "drop";
    private static final String COMMENT = "comment";

    private static String formatByCommand(String category) {
        return "[Command] "
            + NEW_LINE
            + TAP
            + category
            + NEW_LINE
            + "----------------------------------------------------------------------------------------------------";
    }

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared,
        String sql, String url) {
        if (sql.trim().isEmpty()) {
            return formatByCommand(category);
        }
        return formatBySql(sql, category) + getAdditionalMessages(elapsed);
    }

    private String formatBySql(String sql, String category) {
        if (isStatementDDL(sql, category)) {
            return "[DDL] "
                + FormatStyle.DDL.getFormatter()
                .format(sql);
        }

        return "[DML (Read)] "
            + FormatStyle.BASIC.getFormatter()
            .format(sql);
    }

    private String getAdditionalMessages(long elapsed) {
        return NEW_LINE
            + NEW_LINE
            + SPACE + SPACE
            + String.format("[Execution Time] %s ms", elapsed)
            + NEW_LINE
            + "----------------------------------------------------------------------------------------------------";
    }

    private boolean isStatementDDL(String sql, String category) {
        return isStatement(category) && isDDL(sql.trim().toLowerCase(Locale.ROOT));
    }

    private boolean isStatement(String category) {
        return Category.STATEMENT.getName().equals(category);
    }

    private boolean isDDL(String lowerSql) {
        return lowerSql.startsWith(CREATE)
            || lowerSql.startsWith(ALTER)
            || lowerSql.startsWith(DROP)
            || lowerSql.startsWith(COMMENT);
    }
}