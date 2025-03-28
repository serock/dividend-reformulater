// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.FilterOperator;
import com.sun.star.sheet.TableFilterField;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.PivotTableSheetBuilder;
import spreadsheet.sheet.SheetHelper;
import text.Constants;

public class NondividendDistributionsSheetBuilder extends PivotTableSheetBuilder {

    private static final TableFilterField[] filterFields = createFilterFields();

    public NondividendDistributionsSheetBuilder() {
        super();
    }

    @Override
    public void build() throws com.sun.star.uno.Exception {
        final XSpreadsheet nondividendDistributionsSheet = SpreadsheetDocumentHelper.addSheet(document(), "nondividend-distributions");
        final CellAddress cellAddress = SheetHelper.getCellAddress(nondividendDistributionsSheet, 0, 0);

        pivotTableHelper().initialize(nondividendDistributionsSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(Constants.DD_FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(Constants.DD_FIELD_TRANSACTION_TYPE);
        pivotTableHelper().setDataOrientation(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(filterFields);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("nondividend-distributions", cellAddress);

        sheetHelper().updateSheet(nondividendDistributionsSheet, true);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet dividendDetailSheet = SpreadsheetDocumentHelper.getSheet(document(), "dividend-detail");
        return SheetHelper.getCellRangeAddressOfUsedArea(dividendDetailSheet);
    }

    private static TableFilterField[] createFilterFields() {
        final List<TableFilterField> fields = new ArrayList<>(1);

        TableFilterField field;

        field = new TableFilterField();
        field.Field = Constants.DD_FIELD_TRANSACTION_TYPE;
        field.IsNumeric = false;
        field.StringValue = "Nondividend distribution";
        field.Operator = FilterOperator.EQUAL;
        fields.add(field);

        return fields.toArray(new TableFilterField[0]);
    }
}
