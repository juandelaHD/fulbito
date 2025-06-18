import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { ManageTournamentsTable } from "@/components/tables/ManageTournamentsTable.tsx";

export const TournamentManagementScreen = () => {
    return (
        <CommonLayout>
            <div className="p-6 space-y-6">
                <h1 className="text-2xl font-bold">Manage Your Tournaments</h1>
                <ManageTournamentsTable />
            </div>
        </CommonLayout>
    );
};
