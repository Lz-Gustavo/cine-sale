import sys
from flask import Flask, render_template
import requests

app = Flask(__name__)

#Le IP
if sys.argv[1:]:
	HOST=argv[1]
else:
	HOST="localhost"

#Le porta
if sys.argv[2:]:
	PORT=argv[2]
else:
	PORT=8080

@app.route("/")
def main():
	return render_template('index.html')

if __name__ == "__main__":
	app.run(debug=True, host=HOST, port=PORT)
