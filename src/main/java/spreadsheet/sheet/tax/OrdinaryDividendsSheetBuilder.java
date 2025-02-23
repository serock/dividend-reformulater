// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.FilterConnection;
import com.sun.star.sheet.FilterOperator;
import com.sun.star.sheet.TableFilterField;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.PivotTableSheetBuilder;
import spreadsheet.sheet.SheetHelper;
import text.Constants;

public class OrdinaryDividendsSheetBuilder extends PivotTableSheetBuilder {

    private static final TableFilterField[] tableFilterFields = createFilterFields();

    public OrdinaryDividendsSheetBuilder() {
        super();
    }

    @Override
    public void build() throws IllegalArgumentException, com.sun.star.uno.Exception {
        final XSpreadsheet ordinaryDividendsSheet = SpreadsheetDocumentHelper.addSheet(document(), "ordinary");
        final CellAddress cellAddress = SheetHelper.getCellAddress(ordinaryDividendsSheet, 0, 0);

        pivotTableHelper().initialize(ordinaryDividendsSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(Constants.DD_FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(Constants.DD_FIELD_TRANSACTION_TYPE);
        pivotTableHelper().setDataOrientation(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(tableFilterFields);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("ordinary-dividends", cellAddress);

        sheetHelper().updateSheet(ordinaryDividendsSheet);
        SpreadsheetDocumentHelper.setActiveSheet(document(), ordinaryDividendsSheet);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 2);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet dividendDetailSheet = SpreadsheetDocumentHelper.getSheet(document(), "dividend-detail");
        return SheetHelper.getCellRangeAddressOfUsedArea(dividendDetailSheet);
    }

    private static TableFilterField[] createFilterFields() {
        final TableFilterField[] filterFields = new TableFilterField[4];

        filterFields[0] = new TableFilterField();
        filterFields[0].Field = Constants.DD_FIELD_TRANSACTION_TYPE;
        filterFields[0].IsNumeric = false;
        filterFields[0].StringValue = "Qualified dividend";
        filterFields[0].Operator = FilterOperator.EQUAL;

        filterFields[1] = new TableFilterField();
        filterFields[1].Connection = FilterConnection.OR;
        filterFields[1].Field = Constants.DD_FIELD_TRANSACTION_TYPE;
        filterFields[1].IsNumeric = false;
        filterFields[1].StringValue = "Nonqualified dividend";
        filterFields[1].Operator = FilterOperator.EQUAL;

        filterFields[2] = new TableFilterField();
        filterFields[2].Connection = FilterConnection.OR;
        filterFields[2].Field = Constants.DD_FIELD_TRANSACTION_TYPE;
        filterFields[2].IsNumeric = false;
        filterFields[2].StringValue = "Section 199A dividend";
        filterFields[2].Operator = FilterOperator.EQUAL;

        filterFields[3] = new TableFilterField();
        filterFields[3].Connection = FilterConnection.OR;
        filterFields[3].Field = Constants.DD_FIELD_TRANSACTION_TYPE;
        filterFields[3].IsNumeric = false;
        filterFields[3].StringValue = "Short-term capital gain";
        filterFields[3].Operator = FilterOperator.EQUAL;

        return filterFields;
    }
}
