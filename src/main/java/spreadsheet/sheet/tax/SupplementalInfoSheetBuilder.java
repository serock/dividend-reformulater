// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.star.awt.FontWeight;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.TableSortField;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.SheetBuilder;
import text.Constants;

public class SupplementalInfoSheetBuilder extends SheetBuilder {

    public SupplementalInfoSheetBuilder() {
        super();
    }

    @Override
    public void build() throws com.sun.star.uno.Exception {
        final XSpreadsheet supplementalInfoSheet = SpreadsheetDocumentHelper.addSheet(document(), "supplemental-info");
        final SortedMap<String, Object> headerProperties = createHeaderProperties();
        final List<SortedMap<String, Object>> columnProperties = createColumnProperties();
        sheetHelper().setHeaderProperties(headerProperties);
        sheetHelper().setColumnProperties(columnProperties);
        sheetHelper().setSortFields(createSortFields());
        sheetHelper().updateSheet(supplementalInfoSheet);
        SpreadsheetDocumentHelper.setActiveSheet(document(), supplementalInfoSheet);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 1);
    }

    private void addSecurityDescriptionColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addSourceColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addStateColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        final boolean stateColumnIsEmpty = sheetHelper().isColumnEmpty(2, "State");
        if (stateColumnIsEmpty) {
            columnPropertiesItem.put("IsVisible", Boolean.FALSE);
        }
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addPercentageColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getPercentNumberFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addAmountColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private SortedMap<String, Object> createHeaderProperties() {
        final SortedMap<String, Object> headerProperties = new TreeMap<>();
        headerProperties.put("CharWeight", Float.valueOf(FontWeight.BOLD));
        headerProperties.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        return headerProperties;
    }

    private List<SortedMap<String, Object>> createColumnProperties() {
        final List<SortedMap<String, Object>> columnProperties = new ArrayList<>(4);
        addSecurityDescriptionColumnProperties(columnProperties);
        addSourceColumnProperties(columnProperties);
        addStateColumnProperties(columnProperties);
        addPercentageColumnProperties(columnProperties);
        addAmountColumnProperties(columnProperties);
        return columnProperties;
    }

    private static TableSortField[] createSortFields() {
        TableSortField[] sortFields = new TableSortField[2];
        sortFields[0] = new TableSortField();
        sortFields[0].Field = Constants.SI_FIELD_SECURITY_DESCRIPTION;
        sortFields[0].IsAscending = true;

        sortFields[1] = new TableSortField();
        sortFields[1].Field = Constants.SI_FIELD_STATE;
        sortFields[1].IsAscending = true;
        return sortFields;
    }
}
