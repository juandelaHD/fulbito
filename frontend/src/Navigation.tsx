import { Redirect, Route, Switch } from "wouter";

import { LoginScreen } from "@/screens/LoginScreen";
import { SignupScreen } from "@/screens/SignupScreen";
import { CreateFieldScreen } from "@/screens/admin/CreateFieldScreen.tsx";
import { useToken } from "@/services/TokenContext";
import { FieldsScreen } from "@/screens/user/FieldsScreen.tsx";
import { CreateMatchScreen } from "./screens/user/CreateMatchScreen.tsx";
import { AdminHomePage } from "./screens/admin/AdminHomePage.tsx";
import { PlayerHomePage } from "./screens/user/PlayerHomePage.tsx";
import { ForgotPasswordScreen } from "@/screens/ForgotPasswordScreen.tsx";
import { ResetPasswordScreen } from "@/screens/ResetPasswordScreen.tsx";
import {FieldsManagementScreen} from "@/screens/admin/FieldManagementScreen.tsx";
import { SignupInvitationScreen } from "@/screens/SignupWithInvitation.tsx";
import { TeamCreateScreen } from "@/screens/user/TeamCreateScreen.tsx";
import { TeamsScreen } from "@/screens/user/TeamsScreen.tsx";
import { MatchHomeScreen } from "@/screens/user/MatchHomeScreen.tsx";
import { CreateClosedMatchScreen } from "@/screens/user/CreateClosedMatchScreen.tsx";
import { CreateOpenMatchScreen } from "@/screens/user/CreateOpenMatchScreen.tsx";
import { FormTeamsScreen } from "@/screens/user/FormTeamsScreen.tsx";
import {TournamentsScreen} from "@/screens/user/TournamentsScreen.tsx";
import {VerifyEmailScreen} from "@/screens/VerifyEmailScreen.tsx";
import UserProfileScreen from "@/screens/UserProfileScreen";
import SearchUsersScreen from "@/screens/SearchUsersScreen";
import MyMatchesScreen from "@/screens/user/MyMatchesScreen.tsx";
import MyReservationsScreen from "@/screens/user/MyReservationsScreen.tsx";
import { TeamEditScreen } from "./screens/user/TeamEditScreen.tsx";
import { FieldSchedulesScreen } from "@/screens/admin/FieldSchedulesScreen.tsx";

export const Navigation = () => {
  const [tokenState] = useToken();
  switch (tokenState.state) {
    case "LOGGED_IN":
      return (
        <Switch>
          <Route path="/">
              {tokenState.role === "ADMIN" ? <Redirect href="/admin" /> : <Redirect href="/player" />}
          </Route>
          <Route path="/login">
            <LoginScreen />
          </Route>
          <Route path="/fields/management">
              <FieldsManagementScreen />
          </Route>
          <Route path="/fields/:id/schedules">
            <FieldSchedulesScreen />
          </Route >
          <Route path="/fields/create">
            <CreateFieldScreen />
          </Route>
          <Route path="/fields">
            <FieldsScreen />
          </Route>
          <Route path="/matches">
            <MatchHomeScreen />
          </Route>
          <Route path="/matches/create">
            <CreateMatchScreen />
          </Route>
          <Route path="/matches/create/closed">
            <CreateClosedMatchScreen />
          </Route>
          <Route path="/matches/create/open">
            <CreateOpenMatchScreen />
          </Route>
          <Route path="/matches/:id/teams">
            <FormTeamsScreen />
          </Route>
          <Route path="/admin">
            <AdminHomePage />
          </Route>
          <Route path="/player">
            <PlayerHomePage />
          </Route>
          <Route path="/teams/create">
            <TeamCreateScreen />
          </Route>
          <Route path="/teams/edit/:id" component={TeamEditScreen}/>
          <Route path="/teams">
            <TeamsScreen />
          </Route>
          <Route path="/tournaments">
            <TournamentsScreen />
          </Route>
          <Route path="/profile">
            <UserProfileScreen />
          </Route>
          <Route path="/search">
            <SearchUsersScreen />
          </Route>
          <Route path="/my-matches">
            <MyMatchesScreen />
          </Route>
          <Route path="/my-reservations">
            <MyReservationsScreen />
          </Route>
          <Route>
            <Redirect href="/" />
          </Route>
        </Switch>
      );
    case "LOGGED_OUT":
      return (
        <Switch>
          <Route path="/login">
            <LoginScreen />
          </Route>
          <Route path="/signup">
            <SignupScreen />
          </Route>
            <Route path="/verify-email">
                <VerifyEmailScreen />
            </Route>
            <Route path="/forgot-password">
            <ForgotPasswordScreen />
          </Route>
          <Route path="/reset-password">
            <ResetPasswordScreen />
          </Route>
          <Route path="/invite/:token">
            <SignupInvitationScreen />
          </Route>
          <Route>
            {/* DEFAULT */}
            <Redirect href="/login" />
          </Route>
        </Switch>
      );
    default:
      // Make the compiler check this is unreachable
      return tokenState satisfies never;
  }
};
