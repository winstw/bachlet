const input = document.getElementById('chat-input');
const outputArea = document.getElementById('chat-area');
const addTextButton = document.getElementById('add-text');
const getButton = document.getElementById('get');
const askButton = document.getElementById('ask');
const naskButton = document.getElementById('nask');

const socketRoute = document.getElementById('ws-route').value;
const socket = new WebSocket(socketRoute);


socket.addEventListener('open', function (event) {
    socket.send(JSON.stringify({action: "connect", itemType: "user"}))
    });

input.onkeydown= (event) => {
    if (event.key === 'Enter'){
        socket.send(JSON.stringify({message: input.value}));
        input.value = '';
    }
}


addTextButton.onclick = () => socket.send(JSON.stringify({action: 'add', itemType: 'text'}))
//socket.onopen = ()=>  socket.send("New user connected");
socket.onmessage = (event) => {
    outputArea.value += '\n' + event.data;
    this.data = event.data;
}

class BachItem extends React.Component {
    constructor(props){
        super(props)
    }
//     onDoubleClick = () => props.toggleEditable(this.props.id)
    onNewPosition = position => this.props.onNewItemPosition(this.props.id, position)
    render() {return React.createElement(DraggableComponent, {x: this.props.x, y: this.props.y, onNewPosition: this.onNewPosition}, 
            React.createElement(
                'p', 
                {
                    style: {pointerEvents: "none"}, // pour eviter de perdre le "drag"
                    draggable: true, 
                    onChange: (event) => console.log('CHANGED', event)
                }, 
                `${this.props.value}`))
}
}

class BachWidget extends React.Component {
    constructor(props) {
        super(props);
        this.state = {items: []}

        props.socket.onmessage = event => {
            console.log('MESSAGE FROM WS', event.data);
            const {items, users} = JSON.parse(event.data);
            this.setState({items})
            console.log(event.data, this.state.items)
        }

    }
    
    onNewItemPosition(itemId, position){
        const {x: newX, y: newY} = position
        console.log("new position")
        let item = this.state.items.find(item => item.id == itemId)
        item = {...item, x: newX, y: newY};
        console.log(this.state);
    }

    render() {
      return this.state.items.map(({id, value, x, y}) => 
//        React.createElement(Draggable, null, 
            React.createElement(BachItem, {key: id, id, value, x, y, onNewItemPosition: this.onNewItemPosition.bind(this) }, null))
    }
  }
  
  ReactDOM.render(
    React.createElement(BachWidget, {socket}, null),
    document.getElementById('root')
  );

  
