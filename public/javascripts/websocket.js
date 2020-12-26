const input = document.getElementById('chat-input');
const currentUser = document.getElementById('user').innerHTML.split(" ")[1];
const outputArea = document.getElementById('chat-area');
const addTextButton = document.getElementById('add-text');
const addImageButton = document.getElementById('add-image');
const addVideoButton = document.getElementById('add-video');
const socketRoute = document.getElementById('ws-route').value;

class BachItem extends React.Component {
    constructor(props){
        super(props)
    }
    onDoubleClick = () => props.toggleEditable(this.props.id)
    onNewPosition = position => this.props.onNewItemPosition(this.props.id, position)
    render() {
        const {type, value, user} = this.props.value;
        if (type == "user"){
            return React.createElement('i', {style: {/* margin: "10px" */}, className: 'btn btn-info m-2'}, `${value}`);
        }
        const userElement =  React.createElement('div', { 
            style: { pointerEvents: "none", fontSize: "14px"}, 
            draggable: true,
            onChange: (event) => console.log('CHANGED', event)
            }, 
            `${user} : `)
        
        const deleteButtonElement = React.createElement('button', {className: "btn btn-danger btn-sm", onClick: this.props.delete}, 'X')            
        let valueElement;
        if(type =="textItem"){
            valueElement = React.createElement(
                    'p', 
                    {
                        style: {
                            pointerEvents: "none",
                            fontSize: "28px",

                        },
                        className: "card-text",
                        draggable: true, 
                        onChange: (event) => console.log('CHANGED', event)
                    }
                    , `${value}`)
        }else if(type == "imageItem"){
            valueElement = React.createElement(                    
                'img', 
                {
                    style: {
                        pointerEvents: "none",
                    },
                    src: `${value}`,
                    width: "200px",
                    height: "200px"
                }        , null)
        }else {
            valueElement = React.createElement(
                'iframe', {
                    src: `${value}`,
                    width: "200px",
                    height: "200px"
                }
                , null)
            }
        const children = [deleteButtonElement, userElement, valueElement]
        return React.createElement(DraggableComponent, {x: 100*((this.props.id+1)*2), y: 100, onNewPosition: this.onNewPosition}, children)
        
    }
}

class BachWidget extends React.Component {
    constructor(props) {
        super(props);
        this.state = {items: []}
        this.initSocket();
      

    }

    initSocket(){
        const socket = new WebSocket(socketRoute);
        this.socket = socket;
        input.onkeydown= (event) => {
            if (event.key === 'Enter'){
                socket.send(input.value);
                input.value = '';
            }
        }
        socket.onopen = (event) => {
            socket.send(`tells-user-${currentUser}-${currentUser}`)
        }
            
        addTextButton.onclick = () => {
            socket.send(`tells-textItem-${currentUser}-${input.value}`)
            input.value = '';
        }
        
        addImageButton.onclick = () => {
            socket.send(`tells-imageItem-${currentUser}-${input.value}`)
            input.value = '';
        }
        
        addVideoButton.onclick = () => {
            socket.send(`tells-videoItem-${currentUser}-${input.value}`)
            input.value = '';
        }
     
        socket.onmessage = event => {
            console.log("JSON", JSON.parse(event.data))
            this.setState({items:  JSON.parse(event.data)})
        }

        socket.onclose = (e)  => {
            console.log('Socket is closed. Reconnect will be attempted in 1 second.', e.reason);
            setTimeout(() =>  {
              this.initSocket();
            }, 200);
        };
    }

    onDeleteItem = ({type, value}) => () => {
        const command = `gets-${type}-${currentUser}-${value}`
        this.socket.send(command)
    }
    
    onNewItemPosition(itemId, position){
        const {x: newX, y: newY} = position
        console.log("new position")
        let item = this.state.items.find(item => item.id == itemId)
        item = {...item, x: newX, y: newY}
        console.log(this.state)
    }

    render() {
        return this.state.items.map((value, id) => 
          React.createElement(BachItem, {id, value, delete: this.onDeleteItem(value), onNewItemPosition: this.onNewItemPosition.bind(this) }, null));
    }
}

ReactDOM.render(
    React.createElement(BachWidget, {}, null),
    document.getElementById('root')
);
