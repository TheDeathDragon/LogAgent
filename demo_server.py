from flask import Flask, request, jsonify
import json

app = Flask(__name__)

@app.route('/heartbeat', methods=['POST'])
def heartbeat():
    if request.method == 'POST':
        try:
            data = request.get_json()
            deviceImei = json.dumps(data.get('deviceImei'))
            deviceModel = json.dumps(data.get('deviceModel'))
            deviceName = json.dumps(data.get('deviceName'))
            deviceSerialNumber = json.dumps(data.get('deviceSerialNumber'))
            print(f"Imei: {deviceImei} Model: {deviceModel} Name: {deviceName} SN: {deviceSerialNumber}")
            return jsonify({'status': 'success'}), 200
        except Exception as e:
            print(f"Error: {e}")
            return jsonify({'status': 'error', 'message': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=3000)
