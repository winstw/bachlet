const input = document.getElementById('chat-input');
const currentUser = document.getElementById('user').innerHTML.split(" ")[1];
const outputArea = document.getElementById('chat-area');
const addTextButton = document.getElementById('add-text');
const addImageButton = document.getElementById('add-image');
const addVideoButton = document.getElementById('add-video');
const socketRoute = document.getElementById('ws-route').value;

const dragula = require('dragula');

class BachItem extends React.Component {
    constructor(props){
        super(props)
    }

    render() {
        const {type, value, user} = this.props.value;

        const userElement =  <i>{user}</i>
        const deleteButtonElement = <button type="button" className="close" onClick={this.props.delete}>&times;</button>
        let valueElement;

        if(type =="textItem") 
            valueElement = <p style={{fontSize: "20px", flexGrow:1}} className="bg-light"
                                               className="card-text m-3">{value}</p>
       
        else if(type == "imageItem") 
            valueElement = <img src={value} className="card-img"></img>
        
        else valueElement = <iframe src={value} className="card-img"></iframe>

        return  <div style={{width: "300px", textAlign: "center"}} className="card m-3">
                    <div className="card-header p-2 d-flex justify-content-between"> 
                        {userElement} {deleteButtonElement}
                    </div>
                    <div className="card-body">
                        {valueElement}
                    </div>
                </div> 

    }
}

class BachWidget extends React.Component {
    sortMethods = ['value', 'type', 'user']
    constructor(props) {
        super(props);
        const sortMethod = localStorage.getItem('sortMethod');
        this.state = {items: [], users: [], sortMethod: sortMethod ? sortMethod: "value"}
        this.initSocket();
        this.itemContainerRef = React.createRef()
        this.timeout = 125;
    }
    
    componentDidMount() {
         dragula([this.itemContainerRef.current]);
    }

    initSocket(){
        
        const socket = new WebSocket(socketRoute);
        this.socket = socket;
        socket.onopen = (event) => {
            this.timeout = 250;
            socket.send(`tell-user-${currentUser}`)
        }
            
        addTextButton.onclick = () => {
            socket.send(`tells-textItem-${input.value}`)
            input.value = '';
        }
        
        addImageButton.onclick = () => {
            socket.send(`tells-imageItem-${input.value}`)
            input.value = '';
        }
        
        addVideoButton.onclick = () => {
            socket.send(`tells-videoItem-${input.value}`)
            input.value = '';
        }
        input.onkeydown= (event) => {
            if (event.key === 'Enter'){
                addTextButton.onclick()
            }
        }
        socket.onmessage = event => {
            const tokens = JSON.parse(event.data);
            const items = tokens.filter(t => t.type !== "user")
            const users = tokens.filter(t => t.type == "user")
            this.setState({ ...this.state, items, users,})
        }

        socket.onclose = ()  => {
            this.timeout += this.timeout;
            console.log(`Socket is closed. Reconnect will be attempted in ${this.timeout} milliseconds.`);
            setTimeout(() =>  {
              this.initSocket();
            }, this.timeout);
        };
    }

    getComparisonFunctionFor(method){
       return (a,b)=> a[method] == b[method] ? 0 : a[method] > b[method] ? 1 : -1
    }

    onDeleteItem = ({type, value}) => () => {
        const command = `gets-${type}-${value}`
        this.socket.send(command)
    }
    
    setSortMethod(method){
        localStorage.setItem('sortMethod', method)
        this.setState({...this.state, sortMethod: method})
    }
    render() {
        return <div>
            <div className="d-flex justify-content-between pt-3">
                <div className="">
                    <div className="btn-group">
                        {this.sortMethods.map(method => 
                            <button onClick={() => this.setSortMethod(method)} key={method} className={`btn btn-outline-secondary${this.state.sortMethod == method ? ' active': ''}`}>Sort by {method}</button>
                        )}
                    </div>
                </div>
                <div className="d-flex">
                    {this.state.users.map(({value:user}) => 
                        <i key={user} className={`btn btn-${user == currentUser ? 'info' : 'secondary'} ml-1 mr-1`}>{user}</i>
                    )}
                </div>
            </div>
            <div style={{width: "80%", margin: "auto"}}>
                <div  ref={this.itemContainerRef} className="card-columns">
                    {this.state.items.sort(this.getComparisonFunctionFor(this.state.sortMethod)).map((item, id) => 
                    <BachItem id={id} key={item.value} value={item} delete={this.onDeleteItem(item)}>
                    </BachItem>
                    )}
                </div>
            </div>
        </div>

    }
}
  
ReactDOM.render(
    React.createElement(BachWidget, {}, null),
    document.getElementById('root')
);
