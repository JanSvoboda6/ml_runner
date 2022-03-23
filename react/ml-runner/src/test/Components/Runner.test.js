import {act} from "react-dom/test-utils";
import {render, screen} from "@testing-library/react";
import {Router} from "react-router";
import React from "react";
import {createMemoryHistory} from "history";
import axios from "axios";
import Runner from "../../components/project/Runner";
import RunnerService from "../../services/RunnerService";

describe("Running a project", () =>{
    test('When project is in running state then running icon is shown', async () => {
        jest.useFakeTimers();
        jest.spyOn(global, 'setInterval');
        const response = {
            'data': {
                'gammaParameter': 1,
                'cparameter': 1,
                'finished': false
            }
        }
        jest.spyOn(axios, 'get').mockResolvedValue(response);
        await act(async () => {
            render(<Router history={createMemoryHistory()}><Runner/></Router>);
        });

        expect(screen.getByAltText('loading_motion')).toBeInTheDocument();
    });

    test('When project is in end state then running icon is not shown', async () => {
        jest.useFakeTimers();
        jest.spyOn(global, 'setInterval');
        const runnerResponse = {
            'data': {
                'gammaParameter': 1,
                'cparameter': 1
            }
        }
        jest.spyOn(axios, 'get').mockResolvedValue(runnerResponse);

        const statusResponse = {
            'data': {
                'status': 'FINISHED',
                'isEndState': true
            }
        }
        jest.spyOn(RunnerService, 'getStatus').mockResolvedValue(statusResponse)
        await act(async () => {
            render(<Router history={createMemoryHistory()}><Runner/></Router>);
        });

        expect(screen.queryByAltText('loading_motion')).not.toBeInTheDocument();
    });

    test('When project is running then setInterval with recurrent requests is started', async() => {
        jest.useFakeTimers();
        jest.spyOn(global, 'setInterval');
        const response = {
            'data': {
                'gammaParameter': 1,
                'cparameter': 1,
                'finished': false
            }
        }
        jest.spyOn(axios, 'get').mockResolvedValue(response);
        await act(async () => {
            render(<Router history={createMemoryHistory()}><Runner/></Router>);
        });

        expect(setInterval).toHaveBeenCalledTimes(1);
    });

    it.todo('When project is in running state then running time is shown');
})

describe('Stop handling', () =>
{
    it.todo('When stop button is clicked then RunnerService#stop method is called');
});

describe('Result and status handling', () =>
{
    test('When runner is successfully finished then results are shown', async() => {
        jest.useFakeTimers();
        jest.spyOn(global, 'setInterval');
        const initialResponse = {
            'data': {
                'gammaParameter': 1,
                'cparameter': 1,
                'finished': true
            }
        };

        const resultResponse = {
            'data':{
                'firstLabelResult': 0.7,
                'secondLabelResult': 0.8
            }
        };

        const statusResponse = {
            'data':{
                'status': 'FINISHED',
                'isEndState': true
            }
        }

        jest.spyOn(axios, 'get').mockResolvedValueOnce(initialResponse).mockResolvedValueOnce(resultResponse);
        jest.spyOn(RunnerService, 'getStatus').mockResolvedValue(statusResponse);
        await act(async () => {
            render(<Router history={createMemoryHistory()}><Runner/></Router>);
        });

        expect(screen.getByText(/validation result of first label: 70.00%/i)).toBeInTheDocument();
        expect(screen.getByText(/validation result of second label: 80.00%/i)).toBeInTheDocument();
    });

    test('When runner is successfully finished then results are shown', async() => {
        jest.useFakeTimers();
        jest.spyOn(global, 'setInterval');
        const initialResponse = {
            'data': {
                'gammaParameter': 1,
                'cParameter': 1,
                'finished': true
            }
        };

        const resultResponse = {
            'data':{
                'firstLabelResult': 0.7,
                'secondLabelResult': 0.8
            }
        };

        const statusResponse = {
            'data':{
                'status': 'FINISHED',
                'isEndState': true
            }
        }

        jest.spyOn(axios, 'get').mockResolvedValueOnce(initialResponse).mockResolvedValueOnce(resultResponse);
        jest.spyOn(RunnerService, 'getStatus').mockResolvedValue(statusResponse);
        await act(async () => {
            render(<Router history={createMemoryHistory()}><Runner/></Router>);
        });

        expect(screen.getByText(/finished/i)).toBeInTheDocument();
    });

    it.todo('When there is a problem with running the project then error message is shown');
});