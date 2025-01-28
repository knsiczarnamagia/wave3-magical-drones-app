import Spinner from "./Spinner";

interface SpinnerButtonProps {
    onClickHandler: () => void;
    isLoading: boolean;
    text: string;
    loadingText: string;
    className?: string;
    // bgColor: string;
}

export default function SpinnerButton(props: SpinnerButtonProps) {
    return (
        <button 
            className={props.className || ''}
            // style={{ backgroundColor: props.bgColor}}
            onClick={props.onClickHandler} 
            disabled={props.isLoading}
        >
            {props.isLoading ? (
                <div style={{ display: 'flex', alignItems: 'center' }}>
                    <Spinner size="small" />
                    <span style={{ marginLeft: '0.5rem' }}>{props.loadingText}</span>
                </div>
            ) : (
                props.text
            )}
        </button>
    );
}