//package Task_01;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//
//public class ClientHandler implements Runnable{
//    private Socket socket;
//    private BufferedReader in;
//    private PrintWriter out;
//    private String clientId;
//
//    public ClientHandler(Socket socket){
//        this.socket=socket;
//        // Example: "f47ac10b-58cc-4372-a567-0e02b2c3d479"-> Universally Unique Identifier
//        this.clientId= UUID.randomUUID().toString();
//
//    }
//    @Override
//    public void run(){
//        try {
//            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            out=new PrintWriter(socket.getOutputStream(),true);
//            out.println("Welcome to the PubSubBroker. Your ID: "+clientId);
//            String message;
//            while ((message=in.readLine())!=null){
//                //Parse JSON message
//                JSONObject json=new JSONObject();
//                String type=json.getString("type");
//
//                if ("subscribe".equals(type)){
//                    handleSubscribe(json.getJSONArray("topics"));
//                }else if("publish".equals(type)){
//                    handlePublish(json.getString("topic"),json.getString("message"));
//
//                }else if("unsubscribe".equals(type)){
//                    handleUnsubscribe(json.getJSONArray("topics"));
//                }
//            }
//        }catch (IOException e){
//            System.err.println("Client "+clientId+" disconnected: "+e.getMessage());
//        }finally {
//            cleanup();
//        }
//    }
//    public void handleSubscribe(JSONArray topics){
//        for (int i = 0; i < topics.length(); i++) {
//            String topic = topics.getString(i);
//            topicSubscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(out);
//            out.println("SUBSCRIBED: " + topic);
//        }
//    }
//    private void handlePublish(String topic, String message) {
//        List<PrintWriter> subscribers = topicSubscribers.get(topic);
//        if (subscribers != null) {
//            JSONObject json = new JSONObject();
//            json.put("topic", topic);
//            json.put("message", message);
//
//            for (PrintWriter subscriber : subscribers) {
//                subscriber.println(json.toString());
//            }
//        }
//        out.println("PUBLISHED to " + (subscribers != null ? subscribers.size() : 0) + " subscribers");
//    }
//    private void handleUnsubscribe(JSONArray topics) {
//        for (int i = 0; i < topics.length(); i++) {
//            String topic = topics.getString(i);
//            List<PrintWriter> subscribers = topicSubscribers.get(topic);
//            if (subscribers != null) {
//                subscribers.remove(out);
//                out.println("UNSUBSCRIBED: " + topic);
//            }
//        }
//    }
//    private void cleanup() {
//        try {
//            // Remove this client from all topics
//            for (List<PrintWriter> subscribers : topicSubscribers.values()) {
//                subscribers.remove(out);
//            }
//            if (in != null) in.close();
//            if (out != null) out.close();
//            if (socket != null) socket.close();
//        } catch (IOException e) {
//            System.err.println("Error cleaning up client " + clientId + ": " + e.getMessage());
//        }
//    }
//}
//
//
