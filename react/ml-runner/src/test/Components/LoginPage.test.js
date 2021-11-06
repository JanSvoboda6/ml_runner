describe('Rendering', () =>
{
    it.todo('When form is rendered then all fields and submit button are present');
    it.todo('When form is rendered then all fields are empty');
    it.todo('When form is rendered then submit button is disabled');
    it.todo('When there is a login popup parameter then pop window with successful registration is displayed')
});

describe('Logining in', () =>
{
    it.todo('When form is sucessfully submitted then user is redirected to the main page');
    it.todo('When form is not sucessfully submitted then user is not redirected to the main page');
    it.todo('When form is sucessfully submitted then user information are stored in the local storage');
    it.todo('When form is not sucessfully submitted then user information are not stored in the local storage');
    it.todo('When form is submitted then POST request is sent');
    it.todo('When form is submitted and there is an exception on server side then error message is displayed');
    it.todo('When form has been submitted then submit button becomes disabled');
    it.todo('When form has been submitted and there is an error then submit button becomes enabled');
});

describe('Validation', () =>
{
    it.todo('When form is submitted then all fields must be filled');
    it.todo('When fields are not properly filled then submit button is disabled');
    it.todo('When fields are properly filled then submit button is enabled');
    it.todo('When email is filled then email has correct format');
    it.todo('When email is filled then email must be shorter or equal to maximum email length');
    it.todo('When password is filled then password must be shorter or equal to maximum password length');
    it.todo('When email is not in a correct format then the warning message is displayed');
    it.todo('When password is not in a correct length then the warning message is displayed');
});