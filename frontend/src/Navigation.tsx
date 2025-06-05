import { Redirect, Route, Switch } from "wouter";

import { LoginScreen } from "@/screens/LoginScreen";
import { MainScreen } from "@/screens/MainScreen";
import { SignupScreen } from "@/screens/SignupScreen";
import { CreateFieldScreen } from "@/screens/CreateFieldScreen"; 
import { useToken } from "@/services/TokenContext";
import OpenMatchesScreen from "@/screens/OpenMatchesScreen";
import { FieldsScreen } from "@/screens/FieldsScreen";
import { MatchScreen } from "./screens/MatchScreen";
export const Navigation = () => {
  const [tokenState] = useToken();
  switch (tokenState.state) {
    case "LOGGED_IN":
      return (
        <Switch>
          <Route path="/">
            <MainScreen />
          </Route>
         <Route path="/fields/new">
            <CreateFieldScreen />
          </Route>
          <Route path="/fields">
            <FieldsScreen />
          </Route>
          <Route path="/open-matches">
            <OpenMatchesScreen />
          </Route>
          <Route path="/match">
            <MatchScreen />
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
          <Route>
            <Redirect href="/signup" />
          </Route>
        </Switch>
      );
    default:
      // Make the compiler check this is unreachable
      return tokenState satisfies never;
  }
};
