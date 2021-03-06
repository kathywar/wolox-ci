//@Library('wolox-ci')
import com.wolox.*;

def call(ProjectConfiguration projectConfig, def version, def nextClosure) {
    println "elasticsearch called";
    return { variables ->
        /* Build elasticsearch image */
       // docker.image("docker.elastic.co/elasticsearch/elasticsearch:${version}").withRun("-e \"discovery.type=single-node\"") { elasticsearch ->
         //   withEnv(["ELASTICSEARCH_URL=http://elasticsearch:9200"])
            {
                println "using elasticsearch service"
                variables.elasticsearch = elasticsearch;
                nextClosure(variables)
            }
       // }
    }
}
