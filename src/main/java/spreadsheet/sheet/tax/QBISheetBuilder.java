// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.star.awt.FontWeight;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.FilterConnection;
import com.sun.star.sheet.FilterOperator;
import com.sun.star.sheet.TableFilterField;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;
import com.sun.star.uno.Exception;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.PivotTableSheetBuilder;
import spreadsheet.sheet.SheetHelper;
import text.Constants;

public class QBISheetBuilder extends PivotTableSheetBuilder {

    private static final TableFilterField[] filterFields = createFilterFields();

    public QBISheetBuilder() {
        super();
    }

    @Override
    public void build() throws Exception {
        final XSpreadsheet qbiSheet = SpreadsheetDocumentHelper.addSheet(document(), "qbi-by-quarter");
        final CellAddress cellAddress = SheetHelper.getCellAddress(qbiSheet, 0, 0);

        pivotTableHelper().initialize(qbiSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(Constants.DD_FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(Constants.DD_FIELD_QUARTER);
        pivotTableHelper().setDataOrientation(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(filterFields);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("qualified-business-income", cellAddress);

        final String[][] formulas = new String[][] {
            {
                "January — March",
                "January — May",
                "January — August",
                "January — December"
            },
            {
                "=IFERROR(GETPIVOTDATA($A$1; $A$1; $B$1; 1); 0)",
                "=H2+IFERROR(GETPIVOTDATA($A$1; $A$1; $B$1; 2); 0)",
                "=I2+IFERROR(GETPIVOTDATA($A$1; $A$1; $B$1; 3); 0)",
                "=GETPIVOTDATA($A$1; $A$1)"
            }
        };

        SheetHelper.setCellRangeFormulas(qbiSheet, "H1:K2", formulas);

        SortedMap<String, Object> cellRangeProperties = new TreeMap<>();
        cellRangeProperties.put("CharWeight", Float.valueOf(FontWeight.BOLD));
        cellRangeProperties.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        SheetHelper.setCellRangeProperties(qbiSheet, "H1:K1", cellRangeProperties);

        cellRangeProperties = new TreeMap<>();
        cellRangeProperties.put("CharWeight", Float.valueOf(FontWeight.BOLD));
        cellRangeProperties.put("NumberFormat", SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        SheetHelper.setCellRangeProperties(qbiSheet, "H2:K2", cellRangeProperties);

        sheetHelper().updateSheet(qbiSheet, true);
        SpreadsheetDocumentHelper.setActiveSheet(document(), qbiSheet);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 2);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet dividendDetailSheet = SpreadsheetDocumentHelper.getSheet(document(), "dividend-detail");
        return SheetHelper.getCellRangeAddressOfUsedArea(dividendDetailSheet);
    }

    private static TableFilterField[] createFilterFields() {
        final String[] types = new String[] {
                "Section 199A dividend"
                };
        final List<TableFilterField> fields = new ArrayList<>(types.length);

        TableFilterField field;

        for (String type : types) {
            field = new TableFilterField();
            field.Connection = FilterConnection.OR;
            field.Field = Constants.DD_FIELD_TRANSACTION_TYPE;
            field.IsNumeric = false;
            field.StringValue = type;
            field.Operator = FilterOperator.EQUAL;
            fields.add(field);
        }
        return fields.toArray(new TableFilterField[0]);
    }
}
