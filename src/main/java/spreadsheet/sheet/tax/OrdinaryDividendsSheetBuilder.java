// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.FilterConnection;
import com.sun.star.sheet.FilterOperator;
import com.sun.star.sheet.TableFilterField;
import com.sun.star.sheet.XCellAddressable;
import com.sun.star.sheet.XDataPilotTablesSupplier;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;
import com.sun.star.uno.UnoRuntime;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.PivotTableSheetBuilder;
import spreadsheet.sheet.SheetHelper;

public class OrdinaryDividendsSheetBuilder extends PivotTableSheetBuilder {

    private static final TableFilterField[] tableFilterFields = createFilterFields();

    public OrdinaryDividendsSheetBuilder() {
        super();
    }

    @Override
    public void build() throws IllegalArgumentException, com.sun.star.uno.Exception {
        final XSpreadsheet ordinaryDividendsSheet = SpreadsheetDocumentHelper.addSheet(document(), "ordinary-dividends");
        final CellAddress cellAddress = UnoRuntime.queryInterface(XCellAddressable.class, ordinaryDividendsSheet.getCellByPosition(0, 0)).getCellAddress();

        pivotTableHelper().setTablesSupplier(UnoRuntime.queryInterface(XDataPilotTablesSupplier.class, ordinaryDividendsSheet));
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(DividendDetailSheetBuilder.FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(DividendDetailSheetBuilder.FIELD_TRANSACTION_TYPE);
        pivotTableHelper().setDataOrientation(DividendDetailSheetBuilder.FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(DividendDetailSheetBuilder.FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(tableFilterFields);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("ordinary-dividends", cellAddress);

        sheetHelper().updateSheet(ordinaryDividendsSheet);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet dividendDetailSheet = SpreadsheetDocumentHelper.getSheet(document(), "dividend-detail");
        return SheetHelper.getCellRangeAddressOfUsedArea(dividendDetailSheet);
    }

    private static TableFilterField[] createFilterFields() {
        final TableFilterField[] filterFields = new TableFilterField[4];
        setQualifiedDividendFilter(filterFields);
        setNonqualifiedDividendFilter(filterFields);
        setSection199aDividendFilter(filterFields);
        setShortTermCapitalGainFilter(filterFields);
        return filterFields;
    }

    private static void setQualifiedDividendFilter(final TableFilterField[] filterFields) {
        filterFields[0] = new TableFilterField();
        filterFields[0].Field = DividendDetailSheetBuilder.FIELD_TRANSACTION_TYPE;
        filterFields[0].IsNumeric = false;
        filterFields[0].StringValue = "Qualified dividend";
        filterFields[0].Operator = FilterOperator.EQUAL;
    }

    private static void setNonqualifiedDividendFilter(final TableFilterField[] filterFields) {
        filterFields[1] = new TableFilterField();
        filterFields[1].Connection = FilterConnection.OR;
        filterFields[1].Field = DividendDetailSheetBuilder.FIELD_TRANSACTION_TYPE;
        filterFields[1].IsNumeric = false;
        filterFields[1].StringValue = "Nonqualified dividend";
        filterFields[1].Operator = FilterOperator.EQUAL;
    }

    private static void setSection199aDividendFilter(final TableFilterField[] filterFields) {
        filterFields[2] = new TableFilterField();
        filterFields[2].Connection = FilterConnection.OR;
        filterFields[2].Field = DividendDetailSheetBuilder.FIELD_TRANSACTION_TYPE;
        filterFields[2].IsNumeric = false;
        filterFields[2].StringValue = "Section 199A dividend";
        filterFields[2].Operator = FilterOperator.EQUAL;
    }

    private static void setShortTermCapitalGainFilter(final TableFilterField[] filterFields) {
        filterFields[3] = new TableFilterField();
        filterFields[3].Connection = FilterConnection.OR;
        filterFields[3].Field = DividendDetailSheetBuilder.FIELD_TRANSACTION_TYPE;
        filterFields[3].IsNumeric = false;
        filterFields[3].StringValue = "Short-term capital gain";
        filterFields[3].Operator = FilterOperator.EQUAL;
    }
}
