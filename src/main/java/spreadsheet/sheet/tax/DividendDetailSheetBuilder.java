// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.star.awt.FontWeight;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.TableSortField;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.SheetBuilder;

public class DividendDetailSheetBuilder extends SheetBuilder {

    static final int FIELD_SECURITY_DESCRIPTION = 0;
    static final int FIELD_AMOUNT = 5;
    static final int FIELD_TRANSACTION_TYPE = 6;

    public DividendDetailSheetBuilder() {
        super();
    }

    @Override
    public void build() throws IllegalArgumentException, com.sun.star.uno.Exception {
        final SortedMap<String, Object> headerProperties = createHeaderProperties();
        final List<SortedMap<String, Object>> columnProperties = createColumnProperties();
        sheetHelper().setSheetName("dividend-detail");
        sheetHelper().setHeaderProperties(headerProperties);
        sheetHelper().setColumnProperties(columnProperties);
        final XSpreadsheet dividendDetailSheet = SpreadsheetDocumentHelper.getSheet(document(), 0);
        sheetHelper().setSortFields(createSortFields());
        sheetHelper().updateSheet(dividendDetailSheet);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 1);
    }

    private void addSecurityDescriptionColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addCusipColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("IsVisible", Boolean.FALSE);
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addSymbolColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("IsVisible", Boolean.FALSE);
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addStateColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("IsVisible", Boolean.FALSE);
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addDateColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getDateNumberFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addAmountColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addTransactionTypeColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addNotesColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("IsVisible", Boolean.FALSE);
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private SortedMap<String, Object> createHeaderProperties() {
        final SortedMap<String, Object> headerProperties = new TreeMap<>();
        headerProperties.put("CharWeight", Float.valueOf(FontWeight.BOLD));
        headerProperties.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        return headerProperties;
    }

    private List<SortedMap<String, Object>> createColumnProperties() {
        final List<SortedMap<String, Object>> columnProperties = new ArrayList<>(8);
        addSecurityDescriptionColumnProperties(columnProperties);
        addCusipColumnProperties(columnProperties);
        addSymbolColumnProperties(columnProperties);
        addStateColumnProperties(columnProperties);
        addDateColumnProperties(columnProperties);
        addAmountColumnProperties(columnProperties);
        addTransactionTypeColumnProperties(columnProperties);
        addNotesColumnProperties(columnProperties);
        return columnProperties;
    }

    private static TableSortField[] createSortFields() {
        TableSortField[] sortFields = new TableSortField[2];
        sortFields[0] = new TableSortField();
        sortFields[0].Field = 0;
        sortFields[0].IsAscending = true;
        sortFields[1] = new TableSortField();
        sortFields[1].Field = 4;
        sortFields[1].IsAscending = true;
        return sortFields;
    }
}
