import {act} from "react-dom/test-utils";
import {fireEvent, render, screen} from "@testing-library/react";
import {Router} from "react-router";
import React from "react";
import {createMemoryHistory} from "history";
import RunnerForm from "../../components/project/SupportVectorMachinesRunnerForm";
import RunnerService from "../../services/RunnerService";

const submitForm = async () => {
    await act(async () => {
        fireEvent.change(screen.getByTestId(/gamma/i), {target: {value: '1'}});
        fireEvent.change(screen.getByTestId(/c/i), {target: {value: '1'}});
        fireEvent.change(screen.getByTestId(/kernel/i), {target: {value: 'rbf'}});
        fireEvent.click(screen.getByText('Save & Run'));
    });
}

const mockWindowLocationReload = () => {
    delete window.location;
    window.location = Object.assign({'reload': jest.fn()});
}

describe('Run handling', () =>
{
    test('When run button is clicked then RunnerService#run method is called', async() => {
        RunnerService.run = jest.fn();
        await act(async () => {
            render(<Router history={createMemoryHistory()}><RunnerForm handleRunButton={jest.fn()}/></Router>);
        });

        await submitForm();

        expect(RunnerService.run).toHaveBeenCalledTimes(1);
    });

    test('When run button is clicked then window is reloaded', async() => {
        mockWindowLocationReload();

        RunnerService.run = jest.fn();
        await act(async () => {
            render(<Router history={createMemoryHistory()}><RunnerForm handleRunButton={jest.fn()}/></Router>);
        });

        await submitForm();

        expect(window.location.reload).toHaveBeenCalledTimes(1);
    });

});
