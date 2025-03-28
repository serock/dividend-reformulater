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

public class TaxExemptStatesSheetBuilder extends PivotTableSheetBuilder {

    private static final TableFilterField[] filterFields = createFilterFields();

    public TaxExemptStatesSheetBuilder() {
        super();
    }

    @Override
    public void build() throws com.sun.star.uno.Exception {
        final XSpreadsheet taxExemptStatesSheet = SpreadsheetDocumentHelper.addSheet(document(), "tax-exempt-by-state");
        final CellAddress cellAddress = SheetHelper.getCellAddress(taxExemptStatesSheet, 0, 0);

        pivotTableHelper().initialize(taxExemptStatesSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(Constants.SI_FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(Constants.SI_FIELD_STATE);
        pivotTableHelper().setDataOrientation(Constants.SI_FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(Constants.SI_FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(filterFields);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("tax-exempt-by-state", cellAddress);

        sheetHelper().updateSheet(taxExemptStatesSheet, true);
        SpreadsheetDocumentHelper.setActiveSheet(document(), taxExemptStatesSheet);
        SpreadsheetDocumentHelper.freezeColumnsOfActiveSheet(document(), 2);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet supplementalInfoSheet = SpreadsheetDocumentHelper.getSheet(document(), "supplemental-info");
        return SheetHelper.getCellRangeAddressOfUsedArea(supplementalInfoSheet);
    }

    private static TableFilterField[] createFilterFields() {
        final List<TableFilterField> fields = new ArrayList<>(1);

        TableFilterField field;

        field = new TableFilterField();
        field.Field = Constants.SI_FIELD_STATE;
        field.IsNumeric = false;
        field.Operator = FilterOperator.NOT_EMPTY;
        fields.add(field);

        return fields.toArray(new TableFilterField[0]);
    }
}
