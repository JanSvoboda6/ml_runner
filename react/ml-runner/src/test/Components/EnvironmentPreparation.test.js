import axios from "axios";
import {act} from "react-dom/test-utils";
import {render, screen} from "@testing-library/react";
import {Router} from "react-router";
import {createMemoryHistory} from "history";
import ProjectList from "../../components/project/ProjectList";
import React from "react";
import EnvironmentPreparation from "../../components/pages/EnvironmentPreparation";
import DockerService from "../../services/DockerService";

describe("Preparing an environment", () =>{
    test("When environment is prepared then user is redirected to Board page", async () => {
        const history = createMemoryHistory();
        history.push = jest.fn();
        jest.spyOn(DockerService, 'prepareContainer').mockResolvedValue({});

        await act(async () => {
                render(<Router history={history}><EnvironmentPreparation/></Router>);
            });

        await act(async () => {
            await new Promise((response) => {
                setTimeout(response, 2000)
            });
        });
        expect(history.push).toHaveBeenCalledWith('/');
    });
    test("When environment is being prepared then loading animation and text is shown", async () => {
        jest.spyOn(DockerService, 'prepareContainer').mockResolvedValue({});
        await act(async () => {
            render(<Router history={createMemoryHistory()}><EnvironmentPreparation/></Router>);
        });
        expect(screen.getByText('We are preparing your environment...')).toBeInTheDocument();
        expect(screen.getByAltText('loading_motion')).toBeInTheDocument();
    });
    it.todo("When preparation of environment failed then error message is shown and logout action will be proposed");
})