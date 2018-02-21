threadcount = 0

function setup(thread)
  thread:set("counter", threadcount * 10000)
  threadcount = threadcount + 1
end

function init(args)
  wrk.method = "POST"
  wrk.headers["Content-Type"] = "application/json"
end

function request()
  body = "{\"name\": \"" .. counter .. "clown\", \"age\": \"" .. counter .."\"}"
  counter = counter + 1
  return wrk.format(nil, nil, nil, body)
end
