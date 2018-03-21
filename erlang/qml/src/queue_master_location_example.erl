-module(queue_master_location_example).

-include_lib("amqp_client/include/amqp_client.hrl").

%% API
-export([ start/0 ]).

start() ->

  %% Start a network connection to any cluster node
  {ok, Connection} = amqp_connection:start(#amqp_params_network{}),

  %% Open a channel on the connection
  {ok, Channel}    = amqp_connection:open_channel(Connection),

  %% Declare a queue with a queue master location policy
  Queue= <<"microservices.queue.1">>,
  Args = [{<<"x-queue-master-locator">>, longstr, <<"min-masters">>}],
  QueueDeclare = #'queue.declare'{queue      = Queue,
                                  auto_delete= true,
                                  durable    = true,
                                  arguments  = Args },
  #'queue.declare_ok'{} = amqp_channel:call(Channel, QueueDeclare),

  amqp_channel:subscribe(Channel, #'basic.consume'{queue  = Queue,
                                                   no_ack = true}, self()),
  loop(Channel).

loop(Channel) ->
      receive
          %% This is the first message received
          #'basic.consume_ok'{} ->
              loop(Channel);

          %% This is received when the subscription is cancelled
          #'basic.cancel_ok'{} ->
              ok;

          %% Message delivery
          {#'basic.deliver'{delivery_tag = Tag}, Content} ->

              %% Do something with the message content...write to file for example
			  file:write_file( "received_message.txt", Content ),

              %% Acknowledge delivery
			  amqp_channel:cast(Channel, #'basic.ack'{delivery_tag = Tag}),
              loop(Channel)
      end.
