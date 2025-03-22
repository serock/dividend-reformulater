// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.star.sheet.XSpreadsheet;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.SheetBuilder;

public class Form1099DivSheetBuilder extends SheetBuilder {

    @Override
    public void build() throws com.sun.star.uno.Exception {
        final XSpreadsheet form1099DivSheet = SpreadsheetDocumentHelper.addSheet(document(), "form-1099-div");
        sheetHelper().setColumnProperties(createColumnPropertiesCollection());
        sheetHelper().updateSheet(form1099DivSheet, true);
        SpreadsheetDocumentHelper.setActiveSheet(document(), form1099DivSheet);
    }

    private List<SortedMap<String, Object>> createColumnPropertiesCollection() {
        final List<SortedMap<String, Object>> columnPropertiesCollection = new ArrayList<>(3);
        addBoxColumnProperties(columnPropertiesCollection);
        addDescriptionColumnProperties(columnPropertiesCollection);
        addAmountColumnProperties(columnPropertiesCollection);
        return columnPropertiesCollection;
    }

    private static void addNumberFormatColumnProperty(final SortedMap<String, Object> columnProperties, final Integer indexKey) {
        columnProperties.put("NumberFormat", indexKey);
    }

    private void addBoxColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, SpreadsheetDocumentHelper.getTextFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addDescriptionColumnProperties(List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, SpreadsheetDocumentHelper.getTextFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addAmountColumnProperties(List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }
}
