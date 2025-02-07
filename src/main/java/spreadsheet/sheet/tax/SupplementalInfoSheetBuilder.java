// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.star.awt.FontWeight;
import com.sun.star.sheet.XSpreadsheet;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.SheetBuilder;

public class SupplementalInfoSheetBuilder extends SheetBuilder {

    static final int FIELD_SECURITY_DESCRIPTION = 0;
    static final int FIELD_SOURCE = 1;
    static final int FIELD_AMOUNT = 3;

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
        sheetHelper().updateSheet(supplementalInfoSheet);
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
        addPercentageColumnProperties(columnProperties);
        addAmountColumnProperties(columnProperties);
        return columnProperties;
    }
}
