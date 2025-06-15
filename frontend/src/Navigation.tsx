import { Redirect, Route, Switch } from "wouter";

import { LoginScreen } from "@/screens/LoginScreen";
import { MainScreen } from "@/screens/MainScreen";
import { SignupScreen } from "@/screens/SignupScreen";
import { CreateFieldScreen } from "@/screens/CreateFieldScreen"; 
import { useToken } from "@/services/TokenContext";
import { FieldsScreen } from "@/screens/FieldsScreen";
import { CreateMatchScreen } from "./screens/CreateMatchScreen.tsx";
import { AdminHomePage } from "./screens/AdminHomePage";
import { PlayerHomePage } from "./screens/PlayerHomePage";
import { ForgotPasswordScreen } from "@/screens/ForgotPasswordScreen.tsx";
import { ResetPasswordScreen } from "@/screens/ResetPasswordScreen.tsx";
import {FieldsManagementScreen} from "@/screens/FieldManagementScreen.tsx";
import { SignupInvitationScreen } from "@/screens/SignupWithInvitation.tsx";
import { TeamCreateScreen } from "@/screens/TeamCreateScreen.tsx";
import { TeamsScreen } from "@/screens/TeamsScreen.tsx";
import { MatchHomeScreen } from "@/screens/MatchHomeScreen.tsx";
import { CreateClosedMatchScreen } from "@/screens/CreateClosedMatchScreen.tsx";
import { CreateOpenMatchScreen } from "@/screens/CreateOpenMatchScreen.tsx";
import UserProfileScreen from "@/screens/UserProfileScreen";
import SearchUsersScreen from "@/screens/SearchUsersScreen";
import { FormTeamsScreen } from "@/screens/FormTeamsScreen.tsx";
import MyMatchesScreen from "@/screens/MyMatchesScreen.tsx";
import MyReservationsScreen from "@/screens/MyReservationsScreen.tsx";

export const Navigation = () => {
  const [tokenState] = useToken();
  switch (tokenState.state) {
    case "LOGGED_IN":
      return (
        <Switch>
          <Route path="/">
            <MainScreen />
          </Route>
          <Route path="/login">
            <LoginScreen />
          </Route>
          <Route path="/fields/management">
              <FieldsManagementScreen />
          </Route>
          <Route path="/fields/new">
            <CreateFieldScreen />
          </Route>
          <Route path="/fields">
            <FieldsScreen />
          </Route>
          <Route path="/match">
            <MatchHomeScreen />
          </Route>
          <Route path="/match/create">
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
          <Route path="/teams">
            <TeamsScreen />
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
          <Route path="/fields">
            <FieldsScreen />
          </Route>
          <Route path="/signup">
            <SignupScreen />
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
