import pyTigerGraph as tg
import dgl
import networkx as nx
import torch
from gcn import GCN
import torch.nn as nn
import torch.nn.functional as F
import cfg

numEpochs = 10

wantedTopic = "Saxophone"

unwantedTopic = "Falkland_Islands" #In future, determine automatically through the inverse of PageRank/other centrality algo

graph = tg.TigerGraphConnection(ipAddress="https://wikipediagraph.i.tgcloud.us", apiToken=cfg.token, graphname="WikipediaGraph") # connection to graph

articleToNum = {} # translation dictionary for article name to number (for dgl)
numToArticle = {} # translation dictionary for number to article name
i = 0
def createEdgeList(result): # returns tuple of number version of edge
    global i
    if result["article1"] in articleToNum:
        fromKey = articleToNum[result["article1"]]
    else:
        articleToNum[result["article1"]] = i
        numToArticle[i] = result["article1"]
        fromKey = i
        i+=1
    if result["article2"] in articleToNum:
        toKey = articleToNum[result["article2"]]
    else:
        articleToNum[result["article2"]] = i
        numToArticle[i] = result["article2"]
        toKey = i
        i+=1
    return (fromKey, toKey)
    
edges = [createEdgeList(thing) for thing in  graph.runInstalledQuery("tupleArticle", {})["results"][0]["list_of_article_tuples"]] # creates list of edges

wantedNum = articleToNum[wantedTopic]
unwantedNum = articleToNum[unwantedTopic]

g = nx.DiGraph()

g.add_edges_from(edges)

G = dgl.DGLGraph(g)

print('We have %d nodes.' % G.number_of_nodes())
print('We have %d edges.' % G.number_of_edges())


G.ndata["feat"] = torch.eye(G.number_of_nodes()) # one hot encode nodes for features (replace with doc2vec in future)

print(G.nodes[2].data['feat'])


# The first layer transforms input features of size of 34 to a hidden size of 5.
# The second layer transforms the hidden layer and produces output features of
# size 2, corresponding to the two groups of the karate club.
net = GCN(G.number_of_nodes(), 30, 2)

inputs = torch.eye(G.number_of_nodes())
labeled_nodes = torch.tensor([wantedNum, unwantedNum])  # only the instructor and the president nodes are labeled
labels = torch.tensor([0, 1])  # their labels are different


optimizer = torch.optim.Adam(net.parameters(), lr=0.01)
all_logits = []
for epoch in range(numEpochs):
    logits = net(G, inputs)
    # we save the logits for visualization later
    all_logits.append(logits.detach())
    logp = F.log_softmax(logits, 1)
    # we only compute loss for labeled nodes
    loss = F.nll_loss(logp[labeled_nodes], labels)

    optimizer.zero_grad()
    loss.backward()
    optimizer.step()

    print('Epoch %d | Loss: %6.3e' % (epoch, loss.item()))

predictions = list(all_logits[numEpochs-1])

predictionsWithIndex = []
a = 0
for article in predictions:
    predictionsWithIndex.append([a, article[0]]) #article[0] - article[1]
    a+=1

predictionsWithIndex.sort(key=lambda x: x[1], reverse=True)

topResults = predictionsWithIndex[:10]


for article in topResults:
    print("Article Id: "+str(article[0]))
    print("Article Name: "+str(numToArticle[article[0]]))
    print("Article Score: "+str(article[1]))
    print("")




