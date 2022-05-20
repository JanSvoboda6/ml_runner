class Status:
    INITIAL = 'INITIAL'
    LOADING_DATA = 'PREPARING_DATA'
    TRAINING = 'TRAINING'
    PREDICTING = 'PREDICTING'
    FINISHED = 'FINISHED'
    FAILED = 'FAILED'
    CANCELLED = 'CANCELLED'


def status_change(runner_identifier, status):
    with open('runners_info/' + str(runner_identifier) + '/status.txt', 'a') as status_file:
        status_file.write(status + '\n')
