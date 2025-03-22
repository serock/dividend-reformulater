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
import text.Constants;

public class DividendDetailSheetBuilder extends SheetBuilder {

    public DividendDetailSheetBuilder() {
        super();
    }

    @Override
    public void build() throws IllegalArgumentException, com.sun.star.uno.Exception {
        final XSpreadsheet dividendDetailSheet = SpreadsheetDocumentHelper.getSheet(document(), 0);
        final SortedMap<String, Object> headerProperties = createHeaderProperties();
        final List<SortedMap<String, Object>> columnPropertiesCollection = createColumnPropertiesCollection();
        sheetHelper().setSheetName("dividend-detail");
        sheetHelper().setHeaderProperties(headerProperties);
        sheetHelper().setColumnProperties(columnPropertiesCollection);
        sheetHelper().setSortFields(createSortFields());
        sheetHelper().updateSheet(dividendDetailSheet, true);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 1);
    }

    private static void addNumberFormatColumnProperty(final SortedMap<String, Object> columnProperties, final Integer indexKey) {
        columnProperties.put("NumberFormat", indexKey);
    }

    private void addSecurityDescriptionColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, SpreadsheetDocumentHelper.getTextFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addCusipColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        columnProperties.put("IsVisible", Boolean.FALSE);
        addNumberFormatColumnProperty(columnProperties, SpreadsheetDocumentHelper.getTextFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addSymbolColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        columnProperties.put("IsVisible", Boolean.FALSE);
        addNumberFormatColumnProperty(columnProperties, SpreadsheetDocumentHelper.getTextFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addStateColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        columnProperties.put("IsVisible", Boolean.FALSE);
        addNumberFormatColumnProperty(columnProperties, SpreadsheetDocumentHelper.getTextFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addDateColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, SpreadsheetDocumentHelper.getDateNumberFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addAmountColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addTransactionTypeColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, SpreadsheetDocumentHelper.getTextFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addNotesColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        columnProperties.put("IsVisible", Boolean.FALSE);
        addNumberFormatColumnProperty(columnProperties, SpreadsheetDocumentHelper.getTextFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private SortedMap<String, Object> createHeaderProperties() {
        final SortedMap<String, Object> headerProperties = new TreeMap<>();
        headerProperties.put("CharWeight", Float.valueOf(FontWeight.BOLD));
        headerProperties.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        return headerProperties;
    }

    private List<SortedMap<String, Object>> createColumnPropertiesCollection() {
        final List<SortedMap<String, Object>> columnPropertiesCollection = new ArrayList<>(8);
        addSecurityDescriptionColumnProperties(columnPropertiesCollection);
        addCusipColumnProperties(columnPropertiesCollection);
        addSymbolColumnProperties(columnPropertiesCollection);
        addStateColumnProperties(columnPropertiesCollection);
        addDateColumnProperties(columnPropertiesCollection);
        addAmountColumnProperties(columnPropertiesCollection);
        addTransactionTypeColumnProperties(columnPropertiesCollection);
        addNotesColumnProperties(columnPropertiesCollection);
        return columnPropertiesCollection;
    }

    private static TableSortField[] createSortFields() {
        TableSortField[] sortFields = new TableSortField[2];
        sortFields[0] = new TableSortField();
        sortFields[0].Field = Constants.DD_FIELD_SECURITY_DESCRIPTION;
        sortFields[0].IsAscending = true;
        sortFields[1] = new TableSortField();
        sortFields[1].Field = Constants.DD_FIELD_DATE;
        sortFields[1].IsAscending = true;
        return sortFields;
    }
}
