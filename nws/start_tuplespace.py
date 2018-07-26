from nws.client import NetWorkSpace

ts = NetWorkSpace("seats")

ts.store("answer", 42)
count = ts.fetch("answer")
print "The answer is", count
