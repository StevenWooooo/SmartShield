from flask import Flask, request, jsonify
import json
import os, re
from manuf import manuf
import threading

json_file = {}
app = Flask(__name__)


# root
@app.route("/")
def index():
    """
    this is a root dir of my server
    :return: str
    """
    return "This is root!!!!"


# GET
@app.route('/devices')
def hello_user():
    return json.dumps(json_file)

# POST
@app.route('/api/post_some_data', methods=['POST'])
def get_text_prediction():
    """
    predicts requested text whether it is ham or spam
    :return: json
    """
    json = request.get_json()
    print(json)
    if len(json['text']) == 0:
        return jsonify({'error': 'invalid input'})

    return jsonify({'you sent this': json['text']})


def run_detection():
    while True:

        full_results = [re.findall('^[\w\?\.]+|(?<=\s)\([\d\.]+\)|(?<=at\s)[\w\:]+', i) for i in os.popen('arp -a')]
        final_results = [dict(zip(['IP', 'LAN_IP', 'MAC_ADDRESS'], i)) for i in full_results]
        final_results = [{**i, **{'LAN_IP':i['LAN_IP'][1:-1]}} for i in final_results]

        mac=[('MAC_ADDRESS' in final_results[i]) for i in range(len(final_results))]

        local=[final_results[i]['LAN_IP'][:3]=='192' for i in range(len(final_results))]


        addr=[final_results[i]['MAC_ADDRESS'] for i in range(len(final_results)) if mac[i] & local[i]]

        manu=[]
        for i in addr:
            p = manuf.MacParser(update=True)
            tup=p.get_manuf(i)
            manu.append(tup)

        device_list = []
        for n in manu:
            if n == None:
                continue
            if n == 'Apple':
                d = {'name' : 'iPhone X', 'traffic' : '10'}
                device_list.append(d)
            if n == 'WyzeLabs':
                d = {'name' : 'WyzeCam', 'traffic' : '10'}
                device_list.append(d)
        json_file['device_list'] = device_list

        print(manu)


# running web app in local machine
if __name__ == '__main__':
    thread = threading.Thread(target=run_detection)
    thread.start()
    
    app.run(host='127.0.0.1', port=5000)
