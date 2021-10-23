import axios from "axios";
const API_URL = "http://localhost:8080/api/saveproject";

const save = (projectName: any, files: any, selectedModel: any) => 
{
    // console.log(projectName);
    // console.log(files);
    // console.log(selectedModel);
    // const formData = new FormData();
    // formData.append('projectName', projectName);
    // //formData.append('files', files);
    // formData.append('selectedModel', selectedModel);

    axios.post(API_URL, { projectName, selectedModel })
        .then(res =>
        {
            console.log(res);
        })
        .catch(error =>
        {
            var message = "";
            if (error && error.response && error.response.data.message)
            {
                message = error.response.data.message;
            }
            else if (error.message)
            {
                message = error.message;
            }
            else if (error.toString())
            {
                message = error.toString();
            }
            console.log(message);
        })

    //second axios post request with files

    const obj = {
        files: files
    };
    const json = JSON.stringify(obj);
    const blob = new Blob([json], {
        type: 'application/json'
    });
    const formData = new FormData();
    formData.append("document", blob);
};

export default { save }