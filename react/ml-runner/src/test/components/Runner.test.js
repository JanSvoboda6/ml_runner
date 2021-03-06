import {act} from "react-dom/test-utils";
import {render, screen} from "@testing-library/react";
import {Router} from "react-router";
import React from "react";
import {createMemoryHistory} from "history";
import axios from "axios";
import Runner from "../../components/project/Runner";
import RunnerService from "../../services/RunnerService";

describe("Running a project", () => {
    test('When project is in running state then running icon is shown', async () => {
        jest.useFakeTimers();
        jest.spyOn(global, 'setInterval');
        const response = {
            'data': {
                'parameters': [{
                    'name': 'gamma',
                    'value': '1'
                },
                {
                    'name': 'c',
                    'value': '1'
                }]
            }
        }
        jest.spyOn(axios, 'get').mockResolvedValue(response);

        const statusResponse = {
            'data': {
                'status': 'INITIAL',
                'isEndState': false
            }
        }
        jest.spyOn(RunnerService, 'getStatus').mockResolvedValue(statusResponse)

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
                'parameters': [{
                    'name': 'gamma',
                    'value': '1'
                },
                    {
                        'name': 'c',
                        'value': '1'
                    }]
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

    test('When project is running then setInterval with recurrent requests is started', async () => {
        jest.useFakeTimers();
        jest.spyOn(global, 'setInterval');
        const runnerResponse = {
            'data': {
                'parameters': [{
                    'name': 'gamma',
                    'value': '1'
                },
                    {
                        'name': 'c',
                        'value': '1'
                    }]
            }
        }
        jest.spyOn(axios, 'get').mockResolvedValue(runnerResponse);

        const statusResponse = {
            'data': {
                'status': 'RUNNING',
                'isEndState': false
            }
        }
        jest.spyOn(RunnerService, 'getStatus').mockResolvedValue(statusResponse)
        await act(async () => {
            render(<Router history={createMemoryHistory()}><Runner/></Router>);
        });

        expect(setInterval).toHaveBeenCalledTimes(1);
    });
})

describe('Result and status handling', () =>
{
    test('When runner is successfully finished then results are shown', async () => {
        jest.useFakeTimers();
        jest.spyOn(global, 'setInterval');
        const initialResponse = {
            'data': {
                'parameters': [{
                    'name': 'gamma',
                    'value': '1'
                },
                    {
                        'name': 'c',
                        'value': '1'
                    }]
            }
        }

        const resultResponse = {
            'data': {
                'accuracy': 0.7
            }
        };

        const statusResponse = {
            'data': {
                'status': 'FINISHED',
                'isEndState': true
            }
        }

        jest.spyOn(axios, 'get').mockResolvedValueOnce(initialResponse).mockResolvedValueOnce(resultResponse);
        jest.spyOn(RunnerService, 'getStatus').mockResolvedValue(statusResponse);
        await act(async () => {
            render(<Router history={createMemoryHistory()}><Runner/></Router>);
        });

        expect(screen.getByText(/70.00%/i)).toBeInTheDocument();
    });

    test('When runner is successfully finished then finished state is shown', async () => {
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
            'data': {
                'accuracy': 0.7
            }
        };

        const statusResponse = {
            'data': {
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
});