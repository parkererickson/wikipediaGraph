import tgWrapper as tg

graph = tg.TigerGraphConnection(ipAddress="https://wikipediagraph.i.tgcloud.us", apiToken="reko7isjrp6hifchcis87oltea9s7jtk")

"""
authToken = graph.getToken("YOUR SECRET GOES HERE", "1000000")

print(authToken)
"""


#print(graph.getEndpoints())

preInstalledResult = graph.runInstalledQuery("getArticles", {"vid":"Jazz", "vid.type":"Article"})

print(preInstalledResult["results"][0]["result"])


"""
query = "INTERPRET QUERY getArticle () FOR GRAPH MyGraph {start = {Article.*}; result = SELECT all FROM start-()->Article:all; PRINT result;}"

interpretedResult = graph.runInterpretedQuery(query)

print(interpretedResult)
"""


