import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { ManageFieldsTable } from "@/components/tables/ManageFieldsTable.tsx";

export const FieldsManagementScreen = () => {
    return (
        <CommonLayout>
            <div className="p-6 space-y-6">
                <h1 className="text-2xl font-bold">Manage Your Fields</h1>
                <ManageFieldsTable />
            </div>
        </CommonLayout>
    );
};
